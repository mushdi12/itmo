#include <iostream>

using namespace std;

int getBactOnKDay(int a, int b, int c, int d, int k) {
  int curr_day = 0;
  int previous = a;
  while (curr_day < k) {
    if (curr_day > 1 && previous == a) {
      return a;
    }
    previous = a;
    a *= b;
    if (a - c > 0) {
      a -= c;
    } else {
      return 0;
    }
    if (a >= d) {
      a = d;
    }
    curr_day++;
  }
  return a;
}

int main() {
  int a, b, c, d, k;
  cin >> a >> b >> c >> d >> k;
  int res = getBactOnKDay(a, b, c, d, k);
  cout << res;
}