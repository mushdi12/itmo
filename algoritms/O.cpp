#include <iostream>
#include <queue>
#include <vector>
using namespace std;

bool checkBipartite(int start, const vector<vector<int>>& graph, vector<int>& color) {
  queue<int> q;
  color[start] = 1;  
  q.push(start);

  while (!q.empty()) {
    int v = q.front();
    q.pop();
    for (int u : graph[v]) {
      if (color[u] == 0) {
        color[u] = 3 - color[v];  
        q.push(u);
      } else if (color[u] == color[v]) {
        return false;  
      }
    }
  }
  return true;
}

int main() {
  int n, m;
  cin >> n >> m;
  vector<vector<int>> graph(n);
  vector<int> color(n, 0);

  for (int i = 0; i < m; i++) {
    int a, b;
    cin >> a >> b;
    a--;
    b--;
    graph[a].push_back(b);
    graph[b].push_back(a);
  }

  bool isBipartite = true;
  for (int i = 0; i < n; i++) {
    if (color[i] == 0 && !checkBipartite(i, graph, color)) {
      isBipartite = false;
      break;
    }
  }

  cout << (isBipartite ? "YES" : "NO");
  return 0;
}
