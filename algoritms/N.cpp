#include <iostream>
#include <list>
#include <vector>
using namespace std;

void explore(int node, vector<pair<list<int>, int>>& g, int& total) {
  g[node].second = 1;
  for (int neighbor : g[node].first) {
    if (g[neighbor].second == 0) {
      explore(neighbor, g, total);
    } else if (g[neighbor].second == 1) {
      total++;
    }
  }
  g[node].second = 2;
}

int main() {
  int n, t, res = 0;
  cin >> n;
  vector<pair<list<int>, int>> g(n);
  for (int i = 0; i < n; ++i) {
    cin >> t;
    g[t - 1].first.push_back(i);
  }

  for (int i = 0; i < n; ++i) {
    if (g[i].second == 0) {
      explore(i, g, res);
    }
  }

  cout << res;
  return 0;
}
