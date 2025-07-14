package com.mindthekid.geo.cqrs.infrastructure;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.IOException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

public class CognitoAuthorizer implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private final ObjectMapper objectMapper;
    private final String userPoolId;
    private final String region;
    
    public CognitoAuthorizer() {
        this.objectMapper = new ObjectMapper();
        this.userPoolId = System.getenv("COGNITO_USER_POOL_ID");
        this.region = System.getenv("AWS_REGION");
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            // Get the Authorization header
            Map<String, String> headers = request.getHeaders();
            if (headers == null) {
                return createUnauthorizedResponse("Missing Authorization header");
            }
            
            String authHeader = headers.get("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return createUnauthorizedResponse("Invalid Authorization header format");
            }
            
            // Extract the JWT token
            String token = authHeader.substring(7);
            
            // Verify and decode the JWT token
            JWTClaimsSet claimsSet = verifyAndDecodeToken(token);
            if (claimsSet == null) {
                return createUnauthorizedResponse("Invalid or expired token");
            }
            
            // Extract user ID from the token
            String userId = extractUserId(claimsSet);
            if (userId == null) {
                return createUnauthorizedResponse("User ID not found in token");
            }
            
            // Create the authorization response
            Map<String, Object> policyDocument = createPolicyDocument(userId);
            
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(200);
            response.setBody(objectMapper.writeValueAsString(policyDocument));
            response.setHeaders(Map.of("Content-Type", "application/json"));
            
            return response;
            
        } catch (Exception e) {
            context.getLogger().log("Error in Cognito authorizer: " + e.getMessage());
            return createUnauthorizedResponse("Authorization failed: " + e.getMessage());
        }
    }
    
    private JWTClaimsSet verifyAndDecodeToken(String token) {
        try {
            // Parse the JWT token
            SignedJWT signedJWT = SignedJWT.parse(token);
            
            // Get the key ID from the header
            JWSHeader header = signedJWT.getHeader();
            String keyId = header.getKeyID();
            
            // Fetch the public key from Cognito
            RSAPublicKey publicKey = getPublicKey(keyId);
            if (publicKey == null) {
                return null;
            }
            
            // Verify the signature
            JWSObject jwsObject = signedJWT;
            boolean isValid = jwsObject.verify(new RSASSAVerifier(publicKey));
            
            if (!isValid) {
                return null;
            }
            
            // Get the claims
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            
            // Verify the token is not expired
            if (claimsSet.getExpirationTime() != null && 
                claimsSet.getExpirationTime().before(new java.util.Date())) {
                return null;
            }
            
            // Verify the issuer
            String issuer = claimsSet.getIssuer();
            if (issuer == null || !issuer.contains(userPoolId)) {
                return null;
            }
            
            return claimsSet;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private RSAPublicKey getPublicKey(String keyId) {
        try {
            // Construct the JWKS URL
            String jwksUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", 
                region, userPoolId);
            
            // Fetch the JWKS
            URL url = new URL(jwksUrl);
            JWKSet jwkSet = JWKSet.load(url);
            
            // Find the key with the matching key ID
            RSAKey rsaKey = jwkSet.getKeyByKeyId(keyId);
            if (rsaKey == null) {
                return null;
            }
            
            return rsaKey.toRSAPublicKey();
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private String extractUserId(JWTClaimsSet claimsSet) {
        // Try different possible claim names for user ID
        String userId = claimsSet.getSubject(); // sub claim
        
        if (userId == null) {
            // Try custom claims
            Map<String, Object> customClaims = claimsSet.getClaims();
            userId = (String) customClaims.get("cognito:username");
        }
        
        if (userId == null) {
            userId = (String) claimsSet.getClaim("cognito:username");
        }
        
        return userId;
    }
    
    private Map<String, Object> createPolicyDocument(String userId) {
        Map<String, Object> policyDocument = new HashMap<>();
        policyDocument.put("Version", "2012-10-17");
        
        Map<String, Object> statement = new HashMap<>();
        statement.put("Effect", "Allow");
        statement.put("Action", "execute-api:Invoke");
        statement.put("Resource", "*");
        
        policyDocument.put("Statement", new Object[]{statement});
        
        // Add context with user ID
        Map<String, Object> context = new HashMap<>();
        context.put("userId", userId);
        policyDocument.put("context", context);
        
        return policyDocument;
    }
    
    private APIGatewayProxyResponseEvent createUnauthorizedResponse(String message) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(401);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", message);
        
        try {
            response.setBody(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception e) {
            response.setBody("{\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}");
        }
        
        response.setHeaders(Map.of("Content-Type", "application/json"));
        return response;
    }
} 