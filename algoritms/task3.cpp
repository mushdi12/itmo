#include <iostream>
#include <stack>
#include <string>
#include <unordered_map>

using namespace std;

void assignVariable(
    const string& line,
    unordered_map<string, int>& variables,
    stack<pair<string, int>>& changes_stack
) {
  size_t splitter = line.find('=');
  string variable = line.substr(0, splitter);
  string value = line.substr(splitter + 1);

  if (isdigit(value[0]) || (value[0] == '-' && isdigit(value[1]))) {
    if (variables.find(variable) != variables.end()) {
      changes_stack.push({variable, variables[variable]});
    } else {
      changes_stack.push({variable, -1});
    }
    variables[variable] = stoi(value);
  } else {
    if (variables.find(variable) != variables.end()) {
      changes_stack.push({variable, variables[variable]});
    } else {
      changes_stack.push({variable, -1});
    }
    variables[variable] = variables[value];
    cout << variables[value] << endl;
  }
}

int main() {
  unordered_map<string, int> variables;
  stack<pair<string, int>> changes_stack;

  string line;
  while (getline(cin, line)) {
    if (line == "{") {
      changes_stack.push({"", 0});
    } else if (line == "}") {
      while (!changes_stack.empty() && changes_stack.top().first != "") {
        auto [var, value] = changes_stack.top();
        if (value == -1) {
          variables.erase(var);
        } else {
          variables[var] = value;
        }
        changes_stack.pop();
      }
      if (!changes_stack.empty()) {
        changes_stack.pop();
      }
    } else {
      assignVariable(line, variables, changes_stack);
    }
  }

  return 0;
}
