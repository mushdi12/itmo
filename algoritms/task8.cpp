#include <algorithm>
#include <iostream>
#include <vector>

using namespace std;

int getCheapestSum(vector<int>& prices, int n, int k) {
  int total = 0;

  sort(prices.begin(), prices.end());

  for (int i = 0; i < n; ++i) {
    if ((i + 1) % k != 0) {
      total += prices[prices.size() - i - 1];
    }
  }
  return total;
}

int main() {
  int n, k;
  cin >> n >> k;
  vector<int> prices(n);
  for (int& x : prices)
    cin >> x;

  cout << getCheapestSum(prices, n, k) << endl;
  return 0;
}