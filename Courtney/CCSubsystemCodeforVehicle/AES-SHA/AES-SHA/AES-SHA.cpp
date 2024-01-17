#include "cryptlib.h"
#include "rijndael.h"
#include "modes.h"
#include "files.h"
#include "osrng.h"
#include "hex.h"

#include <iostream>
#include <string>

int main(int argc, char* argv[])
{
    using namespace CryptoPP;
    
    /////////////////////////////////////// AES ///////////////////////////////////////
    std::cout << "AES-256 " << std::endl;

    //Setting up The Key, IV, and Cipher/Plain Text Variables
    std::string cipherText;
    std::string plainText;

    AutoSeededRandomPool prng; //defining a variable for random generation which follows cryptography standards
    HexEncoder encoder(new FileSink(std::cout)); //encoder to turn bytes into encoded hex

    SecByteBlock key(AES::DEFAULT_KEYLENGTH); //setting up the lengths of the key byte block
    SecByteBlock iv(AES::BLOCKSIZE); //setting up the lengths of the iv byte block

    prng.GenerateBlock(key, key.size()); //generating the key
    prng.GenerateBlock(iv, iv.size()); //generating the iv

    std::string start = "This will be replaced with the ZHardware Profile"; //String to encrypt and decrypt
    std::string cipher, plain; //setting up the variable for the cipher and recovered plain text

    //Printing out the Starting Phrase, Key, and IV
    std::cout << "Starting Phrase text: " << start << std::endl;

    std::cout << "Key: ";
    encoder.Put(key, key.size());
    encoder.MessageEnd();
    std::cout << std::endl;

    std::cout << "IV: ";
    encoder.Put(iv, iv.size());
    encoder.MessageEnd();
    std::cout << std::endl;

    //Setting up the Instances of Encryption
    CBC_Mode<AES>::Encryption e;
    e.SetKeyWithIV(key, key.size(), iv);

    //Doing the Encryption - Only for demonstration purposes, will be taken out later.
    //Done using Piplining - Takes the data and passes it through the encrytption filter using strings and pointers.
    StringSource s(
        start,
        true,
        new StreamTransformationFilter(e,
            new StringSink(cipher)
        )
    );

    //Outputting the Cipher Text
    std::cout << "Cipher Text: ";
    encoder.Put((const byte*)&cipher[0], cipher.size()); //Using hex encoding to turn it into a readable string
    encoder.MessageEnd();
    std::cout << std::endl;

    //Doing the Decryption
    //Similar to the encryption (setting up the instances then doing the decryption with piplining)
    CBC_Mode <AES>::Decryption d;
    d.SetKeyWithIV(key, key.size(), iv);

    StringSource ss(
        cipher,
        true,
        new StreamTransformationFilter(d, 
            new StringSink(plain))
    );

    std::cout << "Plain Text: " << plain << std::endl;

    /////////////////////////////////////// SHA ///////////////////////////////////////
    std::cout << std::endl << "SHA-256 " << std::endl;

    //Setting up the starting message and the output variable
    std::string msgStart = "This will be replaced with the Dynamic Digital Signature and Command";
    std::string msgDigest;

    SHA256 hashAlgorithm;
    hashAlgorithm.Update((const byte*)msgStart.data(), msgStart.size()); //adds the data to the given hash
    msgDigest.resize(hashAlgorithm.DigestSize()); //resizes the string to the correct size - in this case 256 bits
    hashAlgorithm.Final((byte*)&msgDigest[0]); //does the SHA hash algorithum using a pointer

    std::cout << "Starting Text: " << msgStart << std::endl;

    std::cout << "Final Dynamic Digital Signature Text: ";
    StringSource( //Turning the final binary value into a string
        msgDigest,
        true,
        new Redirector(encoder)); 
    std::cout << std::endl;

    return 0;
}