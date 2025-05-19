#include <algorithm>
#include <iostream>
#include <string>
#include <vector>

using namespace std;

bool comparator(const string& a, const string& b) {
  if (a[0] != b[0]) {
    return a[0] > b[0];
  }

  return a + b > b + a;
}

int main() {
  vector<string> numbers;
  string line;
  while (cin >> line) {
    numbers.push_back(line);
  }

  sort(numbers.begin(), numbers.end(), comparator);

  string answer;
  for (string line : numbers) {
    answer += line;
  }
  cout << answer << endl;
  return 0;
}