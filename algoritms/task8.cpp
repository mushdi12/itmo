#include <cstdint>
#include <iostream>
#include <queue>
#include <set>
#include <unordered_map>
#include <unordered_set>
#include <vector>

using namespace std;

int calculateReplacements(size_t maxCapacity, const vector<int>& requests) {
  unordered_map<int, queue<size_t>> carPositions;
  unordered_set<int> activeCars;
  set<pair<size_t, int>> futureCars;
  int replacements = 0;

  for (size_t i = 0; i < requests.size(); ++i) {
    carPositions[requests[i]].push(i);
  }

  for (size_t i = 0; i < requests.size(); ++i) {
    int currentCar = requests[i];
    carPositions[currentCar].pop();

    if (activeCars.count(currentCar)) {
      futureCars.erase({i, currentCar});
    } else {
      if (activeCars.size() == maxCapacity) {
        auto farthestCar = *futureCars.rbegin();
        futureCars.erase(farthestCar);
        activeCars.erase(farthestCar.second);
      }
      activeCars.insert(currentCar);
      ++replacements;
    }

    if (carPositions[currentCar].empty()) {
      futureCars.emplace(SIZE_MAX, currentCar);
    } else {
      futureCars.emplace(carPositions[currentCar].front(), currentCar);
    }
  }

  return replacements;
}

int main() {
  size_t numCars, maxCapacity, numRequests;
  cin >> numCars >> maxCapacity >> numRequests;

  vector<int> requests(numRequests);
  for (size_t i = 0; i < numRequests; ++i) {
    cin >> requests[i];
  }

  cout << calculateReplacements(maxCapacity, requests) << endl;
  return 0;
}