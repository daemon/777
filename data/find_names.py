import json
import requests
uuids = {}
with open('uuids.csv') as f:
  for line in f.readlines():
    uuid = line.split(',')[1]
    player_id = int(line.split(',')[0])
    uuids[player_id] = uuid.replace('\n', '')
records = []
with open('ranking.csv') as f:
  for line in f.readlines():
    data = line.split(',')
    try:
      player_id = int(data[1])
      ratio = float(data[2])
      uuid = uuids[player_id]
      response = requests.get('https://api.mojang.com/user/profiles/%s/names' % uuid).content.decode('utf-8')
      name = json.loads(response)[-1]['name']
      print(name)
      records.append((player_id, ratio, name))
    except ValueError:
      continue
with open('ranks.csv', 'w') as f:
  f.write(str(records))