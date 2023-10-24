#include <iostream>
#include <cmath>

#define MOD 1000000007
using namespace std;

long long fast_power(long long base, long long power) {
    long long result = 1;
    while(power > 0){
        if(power % 2 == 1) {
            result = (result*base) % MOD;
        }
    base = (base * base) % MOD;
    power = power / 2;
    }
return result;
}


int main(){
    //##### Diffie-Hellman Key Exchange Before Going to Device #####
    //initializing P and G
    int P;
    P = 947;
    int G;
    G = 7;
    long long randInt;

    //Choosing a random number B
    srand((unsigned) time(NULL));
    long long b = 1 + (rand() % 6);

    //Doing the Diffie-Hellman Mathematics
    long long Bprime;
    Bprime = static_cast<long long>(pow(G, b)) % P;

    //sending the value to the device
    std::cout << "B Value to Device: " << Bprime << "\n";
    
    //##### Diffie-Hellman Key Exhcange After Going to Vehicle #####
    //getting the value from the device
    long long A;
    std::cout << "Type in A from Device: ";
    std::cin >> A;

    long long sk;
    sk = static_cast<long long>(pow(A, b)) % P;
    std::cout << "Secret Key: " << sk << "\n";

    return 0;
}