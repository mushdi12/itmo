#include <deque>
#include <iostream>
#include <vector>

using namespace std;

int main() {
  int n, k;
  cin >> n >> k;
  vector<int> nums(n);

  for (int& num : nums) {
    cin >> num;
  }

  deque<int> dq;

  for (int i = 0; i < n; ++i) {
    while (!dq.empty() && dq.front() <= i - k) {
      dq.pop_front();
    }

    while (!dq.empty() && nums[dq.back()] >= nums[i]) {
      dq.pop_back();
    }

    dq.push_back(i);

    if (i >= k - 1) {
      cout << nums[dq.front()] << " ";
    }
  }

  return 0;
}