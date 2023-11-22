#include "cryptlib.h"
#include "sha.h"
#include "filters.h"
#include <iostream>
#include <string>

int main(){
    using namespace CryptoPP;
    
    //Setting up the Input and Output Variables
    std::string plainText = "TestingHashIsOutputting";
    std::string cipherText;

    //Doing the SHA-256 Hash
    SHA256 hash;
    hash.Update((const byte*)plainText.data(), plainText.size());
    cipherText.resize(hash.DigestSize());
    hash.Final((byte*)&cipherText[0]);

    std::cout << "Plain Text:" << plainText << std::endl;

    //Configuring the Output into a String
    std::cout << "Dynamic Digital Signature: ";
    CryptoPP::StringSource ss(
        cipherText,
        true,
        new CryptoPP::HashFilter(
            hash,
            new CryptoPP::StringSink(cipherText)));
    std::cout << std::endl;

    return 0;
}