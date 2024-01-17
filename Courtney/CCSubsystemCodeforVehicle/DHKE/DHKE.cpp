#include <iostream>
#include <cmath>

#define MAX 100000
typedef long long ll;
using namespace std;

//Functions to Enable C++ to Do Power and Mod Mathematics with Larger Numbers
int multiply (int x, int res[], int res_size){
    /* Function does multiplication one array element at a time
    as to accomodate larger numbers. */
    int carry = 0;
    for (int i = 0; i < res_size; i++){
        int prod = res[i] * x + carry;
        res[i] = prod % 10;
        carry = prod / 10;
    }

    while (carry) {
        res[res_size]  = carry % 10;
        carry = carry/10;
        res_size++;
    }
    return res_size;
}

string power(int x, int n){
    /* Does the power manually to allow for larger numbers.
    The final result is put in a string to be used for the DHKE later. */
    if (n == 0){ //setting the resulting power to 1 is the input is 0
        string answer;
        answer = "1";
        return answer;
    }

    int res[MAX];
    int res_size = 0;
    int temp = x;

    while (temp !=0) {
        res[res_size++] = temp % 10;
        temp = temp / 10;
    }

    for (int i = 2; i <= n; i++)
        res_size = multiply(x, res, res_size);
    
    string result;
    for (int i = res_size - 1; i >= 0; i--){
        result = result + to_string(res[i]);
    }
    return result;
}

int mod (string num, int a){ //Does the Modulus Function for larger numbers
    int res = 0;
    for (int i = 0; i < num.length(); i++)
        res = (res * 10 + num[i] - '0') % a; //Property that allows for easier modulating of numbers
    
    return res;
}


int main(){
    //##### Diffie-Hellman Key Exchange Before Going to Device #####
    //Initializing P and G
    int P;
    P = 653;
    int G;
    G = 5;
    long long randInt;

    //Generating a random number B
    srand((unsigned) time(NULL));
    long long b = 1 + (rand() % 6);

    //Doing the Diffie-Hellman Mathematics - G^b mod P
    long long Bprime;
    Bprime = static_cast<long long>(pow(G, b)) % P;
    
    /* Attempted code to get the DHKE to work for larger numbers.
    string BprimePow;
    int Bprime;
    BprimePow = power(G, b);
    Bprime = mod(BprimePow, P);
    */

    //Sending the value to the device - outputting for demonstration purposes
    std::cout << "B Value to Device: " << Bprime << "\n";
    
    //##### Diffie-Hellman Key Exhcange After Going to Vehicle #####
    //Getting the value from the Device
    long long A;
    std::cout << "Type in A from Device: ";
    std::cin >> A;
    
    //Doing the Diffie-Hellman Mathematics - A^b mod P
    long long sk;
    sk = static_cast<long long>(pow(A, b)) % P;
    
    /* Attempted code to get the DHKE to work for larger numbers.
    string skPow;
    int sk;
    skPow = power(A, b);
    sk = mod(skPow, P);
    */
    
    //Outputting the final number for demonstation purposes
    std::cout << "Secret Key: " << sk << "\n";

    return 0;
}