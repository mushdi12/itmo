#include <iostream>
#include <stack>
#include <string>
#include <vector>

using namespace std;

bool matchTrapsAndAnimals(const string& str, vector<int>& matches) {
  stack<char> chars;
  stack<int> animals;
  stack<int> traps;
  int animalCount = 0;

  for (size_t i = 0; i < str.size(); ++i) {
    if (islower(str[i])) {
      animalCount++;
      animals.push(animalCount);
    } else {
      traps.push(i - animalCount);
    }

    if (chars.empty() || str[i] == chars.top()) {
      chars.push(str[i]);
    } else if (tolower(str[i]) == tolower(chars.top())) {
      matches[traps.top()] = animals.top();
      traps.pop();
      animals.pop();
      chars.pop();
    } else {
      chars.push(str[i]);
    }
  }

  return chars.empty();
}

int main() {
  string input;
  cin >> input;

  vector<int> matches(input.size() / 2);
  bool isPossible = matchTrapsAndAnimals(input, matches);

  if (isPossible) {
    cout << "Possible" << endl;
    for (size_t i = 0; i < matches.size(); ++i) {
      cout << matches[i] << " ";
    }
    cout << endl;
  } else {
    cout << "Impossible" << endl;
  }

  return 0;
}