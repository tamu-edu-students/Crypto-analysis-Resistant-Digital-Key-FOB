#include <iostream>
#include <cmath>

#define MAX 100000
typedef long long ll;
using namespace std;

int main(){
    //##### Diffie-Hellman Key Exchange Before Going to Device #####
    //Initializing P and G
    int P;
    P = 13;
    int G;
    G = 7;
    long long randInt;

    //Generating a random number B
    srand((unsigned) time(NULL));
    long long b = 1 + (rand() % 15);

    //Doing the Diffie-Hellman Mathematics - G^b mod P
    long long Bprime;
    Bprime = static_cast<long long>(pow(G, b)) % P;

    //Sending the value to the device - outputting for demonstration purposes
    std::cout << "B Value to Device: " << Bprime << "\n";
    
    //##### Diffie-Hellman Key Exchange After Going to Vehicle #####
    //Getting the value from the Device
    long long A;
    std::cout << "Type in A from Device: ";
    std::cin >> A;
    
    //Doing the Diffie-Hellman Mathematics - A^b mod P
    long long sk;
    sk = static_cast<long long>(pow(A, b)) % P;
    
    //Outputting the final number for demonstation purposes
    std::cout << "Secret Key: " << sk << "\n";

    return 0;
}