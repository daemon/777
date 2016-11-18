import csv
import math
import random
graph = {}
with open('win_records.csv') as file:
  reader = csv.reader(file)
  for row in reader:
    try:
      graph[int(row[1])][int(row[2])] = float(row[5])
    except KeyError:
      graph[int(row[1])] = {}
      graph[int(row[1])][int(row[2])] = float(row[5])
    except ValueError:
      continue
inferred_graph = {}
inferred_info = {}
for player1 in graph:
  for player2 in graph[player1]:
    win_rate1 = graph[player1][player2]
    try:
      for player3 in graph[player2]:
        win_rate2 = graph[player2][player3]
        inferred_ratio = win_rate2 / (1 - win_rate2) * win_rate1 / (1 - win_rate1)
        inferred_rate = 1 / (1 / inferred_ratio + 1)
        try:
          inferred_info[player1]
          inferred_graph[player1]
        except KeyError:
          inferred_info[player1] = {}
          inferred_graph[player1] = {}
        try:
          inferred_info[player1][player3]
          inferred_graph[player1][player3]
        except KeyError:
          inferred_info[player1][player3] = []
          inferred_graph[player1][player3] = []
        inferred_info[player1][player3] = player2
        inferred_graph[player1][player3].append(inferred_rate)
    except KeyError:
      continue
errors = []
data = []
inferred_data = []
for player1 in inferred_graph:
  for player2 in inferred_graph[player1]:
    inferred_rate = inferred_graph[player1][player2]
    inferred_rate = sum(inferred_rate) / len(inferred_rate)
    try:
      mid_player = inferred_info[player1][player2]
      true_rate = graph[player1][player2]
    except KeyError:
      inferred_data.append((player1, player2, inferred_rate))
      continue
    dx = (true_rate - inferred_rate) / inferred_rate
    errors.append(dx)
    data.append((player1, mid_player, player2, graph[player1][mid_player], graph[mid_player][player2], true_rate, inferred_rate))
    if abs(dx) > 0.2:
      print(true_rate, inferred_rate)
with open('errors.csv', 'w') as file:
  file.write(','.join(map(str, errors)))
with open('inferred_test.csv', 'w') as file:
  file.write('\n'.join(map(lambda x: ','.join(map(str, x)), data)))
with open('inferred_data.csv', 'w') as file:
  file.write('\n'.join(map(lambda x: ','.join(map(str, x)), inferred_data)))
print(sum(errors) / len(errors))