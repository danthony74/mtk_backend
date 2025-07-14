import json

INPUT_JSON = 'data/subdivisions.json'
OUTPUT_SQL = 'data/insert_country_states.sql'

with open(INPUT_JSON, 'r', encoding='utf-8') as f:
    subdivisions = json.load(f)

sql_lines = []
for sub in subdivisions:
    country_code = sub['countryAlpha2']
    state_name = sub['name'].replace("'", "''")  # Escape single quotes
    # ISO 3166-2 code is like 'US-CA', we want 'CA' as state_code
    state_code = sub['alpha3'].split('-', 1)[-1] if '-' in sub['alpha3'] else sub['alpha3']
    sql_lines.append(f"((SELECT id FROM country WHERE encoding = '{country_code}'), '{state_name}', '{state_code}')")

with open(OUTPUT_SQL, 'w', encoding='utf-8') as f:
    f.write('-- Auto-generated insert script for country_state table using ISO 3166-2 subdivisions\n')
    f.write('INSERT INTO country_state (country_id, state_name, state_code) VALUES\n')
    f.write(',\n'.join(sql_lines))
    f.write(';\n')

print(f"Generated {len(sql_lines)} rows in {OUTPUT_SQL}") 