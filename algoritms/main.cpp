#include <iostream>
#include <string>
#include <vector>

using namespace std;

string isSquareLine(const string& word) {
  int len = word.length();

  if (len % 2 != 0) {
    return "NO";
  }

  string left = word.substr(0, len / 2);
  string right = word.substr(len / 2, len);

  if (left == right) {
    return "YES";
  } else {
    return "NO";
  }
}

vector<string> isSquareLines(const vector<string>& words) {
  vector<string> results;
  for (const string& w : words) {
    results.push_back(isSquareLine(w));
  }
  return results;
}

int main() {
  int n;
  cin >> n;

  vector<string> words(n);

  for (int i = 0; i < n; i++) {
    cin >> words[i];
  }

  vector<string> result = isSquareLines(words);

  for (string res : result) {
    cout << res << endl;
  }
  
}
