#include <algorithm>
#include <iostream>
#include <unordered_map>
#include <vector>
#include <climits>

using namespace std;

bool customCompare(const pair<char, int>& a, const pair<char, int>& b) {
  return a.second > b.second;
}

unordered_map<char, int> calculateFrequency(const string& input) {
  unordered_map<char, int> frequency;
  for (char ch : input) {
    frequency[ch]++;
  }
  return frequency;
}

vector<pair<char, int>> readLetterValues() {
  vector<pair<char, int>> letterValues(26);
  for (int i = 0; i < 26; i++) {
    cin >> letterValues[i].second;
    letterValues[i].first = 'a' + i;
  }
  sort(letterValues.begin(), letterValues.end(), customCompare);
  return letterValues;
}

string constructResult(
    unordered_map<char, int>& frequency, const vector<pair<char, int>>& letterValues
) {
  string leftPart, rightPart;
  for (const auto& entry : letterValues) {
    char ch = entry.first;
    if (frequency[ch] >= 2) {
      leftPart += ch;
      rightPart = ch + rightPart;
      frequency[ch] -= 2;
    }
  }

  string middlePart;
  for (const auto& entry : frequency) {
    if (entry.second > 0) {
      middlePart += string(entry.second, entry.first);
    }
  }

  return leftPart + middlePart + rightPart;
}

int main() {
  string input;
  cin >> input;

  unordered_map<char, int> frequency = calculateFrequency(input);
  vector<pair<char, int>> letterValues = readLetterValues();

  cout << constructResult(frequency, letterValues);

  return 0;
}
