#include <iostream>

using namespace std;
#include <vector>

bool proverkaRastanovki(vector<int>& stalls, int k, int dist) {
  int cnt = 1;
  int curr_cow = stalls[0];
  for (size_t i = 1; i < stalls.size(); i++) {
    if (-curr_cow + stalls[i] >= dist) {
      curr_cow = stalls[i];
      cnt++;
    }
    if (cnt == k) {
      return true;
    }
  }
  return false;
}

int binSearch(vector<int>& stalls, int k) {
  int left = 0;
  int right = stalls[stalls.size() - 1] - stalls[0] + 1;
  while (left + 1 < right) {
    int mid = (left + right) / 2;
    if (proverkaRastanovki(stalls, k, mid)) {
      left = mid;
    } else {
      right = mid;
    }
  }
  return left;
}

int main() {
  int n, k;
  cin >> n >> k;
  vector<int> stalls(n);
  for (int& x : stalls)
    cin >> x;
  int answer = binSearch(stalls, k);
  cout << answer << endl;
  return 0;
}