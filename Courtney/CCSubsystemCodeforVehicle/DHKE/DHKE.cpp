#include <iostream>
#include <cmath>

int main(){
    //##### Diffie-Hellman Key Exchange Before Going to Device #####
    //initializing P and G
    long long int P;
    P = 1339781092854590957;
    int G;
    G = 5;
    long long randInt;

    //Choosing a random number B
    srand((unsigned) time(NULL));
    int b = 1 + (rand() % 20);

    //Doing the Diffie-Hellman Mathematics
    long long int Bpower;
    Bpower = pow(G, b);
    long long Bprime;
    Bprime = Bpower % P;

    //sending the value to the device
    std::cout << "B Value to Device: " << Bprime << "\n";
    
    //##### Diffie-Hellman Key Exhcange After Going to Vehicle #####
    //getting the value from the device
    long long A;
    std::cout << "Type in A from Device: ";
    std::cin >> A;

    long long sk1;
    sk1 = lround(pow(A, b));
    long long sk;
    sk = sk1 % P;
    std::cout << "\n" << "Secret Key: " << sk;

    return 0;
}