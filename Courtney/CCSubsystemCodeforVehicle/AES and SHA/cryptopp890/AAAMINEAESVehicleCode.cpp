#include "cryptlib.h"
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
    
    std::vector<uint8_t> key{0x64, 0x7b, 0x5e, 0x57, 0x67, 0x1f, 0x2c, 0x10, 0x43, 0x25, 0x0a, 0x25, 0x72, 0x12, 0x49, 0x03};
    std::string keyS(key.begin(), key.end());
    std::cout << "Key Value: " << keyS;
    
    std::vector<uint8_t> iv{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
    std::string ivS(iv.begin(), iv.end());
    std::cout << "IV Value: " << ivS;

    //Setting up AES instances
    auto aes = CryptoPP::AES::Decryption(key.data(), key.size());
    auto aes_cbc = CryptoPP::CBC_Mode_ExternalCipher::Decryption(aes, iv.data());

    //Doing the Decryption
   CryptoPP::StringSource ss(
    cipher,
    true,
    new CryptoPP::StreamTransformationFilter(
        aes_cbc,
        new CryptoPP::StringSink(plainText)
        )
    );

    std::cout << "Resulting Plaintext: " << plainText;

    return 0;
}