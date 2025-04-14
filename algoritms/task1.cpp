#include <iostream>
#include <map>
#include <vector>

using namespace std;

pair<int, int> findMaxUniqueSequence(const vector<int>& flowers, int n) {
  int left = 0;
  pair<int, int> result = {0, 0};
  int mx_len = 0;

  if (n < 3) {
    return {0, n - 1};
  }

  for (int right = 0; right < n; ++right) {

    if (right >= 2 && flowers[right] == flowers[right - 1] &&
        flowers[right] == flowers[right - 2]) {
      left = right - 1;
    }

    if (right - left + 1 > mx_len) {
      mx_len = right - left + 1;
      result = {left, right};
    }
  }

  return result;
}

int main() {
  int n;
  cin >> n;

  vector<int> flowers(n);

  for (int i = 0; i < n; i++) {
    cin >> flowers[i];
  }
  pair<int, int> result = findMaxUniqueSequence(flowers, n);

  cout << result.first + 1 << " " << result.second + 1 << endl;

  return 0;
}