#include <deque>
#include <iostream>

using namespace std;

int main() {
  deque<int> firstHalf, secondHalf;

  int n, goblin;
  cin >> n;
  char operation;

  for (int i = 0; i < n; ++i) {
    cin >> operation;
    switch (operation) {
      case '+':
        cin >> goblin;
        secondHalf.push_back(goblin);
        break;
      case '-':
        cout << firstHalf.front() << endl;
        firstHalf.pop_front();
        break;
      case '*':
        cin >> goblin;
        firstHalf.push_back(goblin);
        break;
    }

    if (firstHalf.size() < secondHalf.size()) {
      firstHalf.push_back(secondHalf.front());
      secondHalf.pop_front();
    } else if (firstHalf.size() > secondHalf.size() + 1) {
      secondHalf.push_front(firstHalf.back());
      firstHalf.pop_back();
    }
  }

  return 0;
}