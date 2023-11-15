#include "cyptolib.h"
#include "rijndael.h"
#include "modes.h"
#include "files.h"
#include "osrng.h"
#include "hex.h"

#include <iostream>
#include <string>

int main (){
    using namespace CryptoPP;
    std::string plainText;

    //Create Vectors for the Encrypted Data, Key, and IV
    std::string cipher;
    std::cout << "Type in Cipher Text: ";
    std::cin >> cipher;
    std::vector<uint8_t> key;
    std::cout << "Key Value: " << key;
    std::vector<uint8_t> iv;
    std::cout << "IV Value: " << key;

    //Setting up AES instances
    auto aes = CryptoPP::AES::Decryption(key.data(), key.size())
    auto aes_cbc = CryptoPP::CBC_Mode_ExternalCipher::Decryption(aes, iv.data());

    //Doing the Decryption
   CyptoPP::StringSource ss(
    cipher,
    true,
    newCryptoPP::StreamTransformationFilter(
        aes_cbc,
        new CryptoPP::StringSink(plainText)
        )
    );

    cout << "Resulting Plaintext: " << plainText;

    return 0;
}