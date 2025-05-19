#include <algorithm>
#include <iostream>
#include <queue>
#include <string>
#include <vector>
using namespace std;

struct Node {
  int weight, index;
  Node(int w = -1, int i = -1) : weight(w), index(i) {
  }
  bool operator<(const Node& other) const {
    return weight > other.weight;
  }
};

struct InputData {
  int rows, cols, start, goal;
  vector<string> grid;
};

InputData read_input() {
  int N, M, sx, sy, gx, gy;
  cin >> N >> M >> sx >> sy >> gx >> gy;
  vector<string> field(N);
  for (auto& row : field)
    cin >> row;
  int s = (sx - 1) * M + (sy - 1);
  int g = (gx - 1) * M + (gy - 1);
  return {N, M, s, g, field};
}

vector<Node> find_shortest_path(const InputData& data) {
  int total = data.rows * data.cols;
  vector<Node> trace(total);
  priority_queue<Node> q;
  q.emplace(0, data.start);

  vector<pair<int, int>> directions = {
      { 0,  1},
      { 0, -1},
      { 1,  0},
      {-1,  0}
  };

  while (!q.empty()) {
    Node current = q.top();
    q.pop();

    int x = current.index / data.cols;
    int y = current.index % data.cols;

    if (data.grid[x][y] == '#')
      continue;
    if (current.index == data.goal)
      break;

    for (auto [dx, dy] : directions) {
      int nx = x + dx, ny = y + dy;
      if (nx < 0 || ny < 0 || nx >= data.rows || ny >= data.cols)
        continue;
      if (data.grid[nx][ny] == '#')
        continue;

      int nid = nx * data.cols + ny;
      int cost = (data.grid[nx][ny] == 'W') ? 2 : 1;
      int new_weight = current.weight + cost;

      if (trace[nid].weight == -1 || trace[nid].weight > new_weight) {
        q.emplace(new_weight, nid);
        trace[nid] = {new_weight, current.index};
      }
    }
  }

  return trace;
}

string reconstruct_path(const vector<Node>& trace, int start, int goal, int cols) {
  if (trace[goal].weight == -1)
    return "";

  string result;
  int current = goal;
  while (current != start) {
    int prev = trace[current].index;
    int diff = current - prev;
    if (diff == 1)
      result += 'E';
    else if (diff == -1)
      result += 'W';
    else if (diff == cols)
      result += 'S';
    else if (diff == -cols)
      result += 'N';
    current = prev;
  }

  reverse(result.begin(), result.end());
  return result;
}

int main() {
  InputData data = read_input();
  vector<Node> trace = find_shortest_path(data);

  cout << trace[data.goal].weight << endl;
  if (trace[data.goal].weight == -1)
    return 0;

  string path = reconstruct_path(trace, data.start, data.goal, data.cols);
  cout << path << endl;

  return 0;
}
