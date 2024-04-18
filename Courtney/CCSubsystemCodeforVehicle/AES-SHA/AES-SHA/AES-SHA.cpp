#include "cryptlib.h"
//#include "rijndael.h"
#include "modes.h"
#include "files.h"
#include "osrng.h"
#include "hex.h"
#include "sha.h"
//#include "base64.h"

#include <iostream>
#include <string>

int main(int argc, char* argv[])
{
    //using namespace CryptoPP;
    
    /*
    /////////////////////////////////////// AES ///////////////////////////////////////
    std::cout << "AES-256 " << std::endl;

    //Setting up The Key, IV, and Cipher/Plain Text Variables
    std::string cipherText;
    std::string plainText;
    std::string AESKey;
    std::string IvVec;

    HexEncoder encoder(new FileSink(std::cout)); //encoder to turn bytes into encoded hex

    AESKey = "AESEncryptionKey";
    IvVec = "InitalizationVec";
    //std::vector<unsigned char> IVecV{ 0x09, 0x1b, 0x17, 0x02, 0x6e, 0x24, 0x23, 0x08, 0x19, 0x0d, 0x4a, 0x10, 0x77, 0x46, 0x7e, 0x32 };

    SecByteBlock EncryptKey(reinterpret_cast<const byte*>(&AESKey[0]), AESKey.size());
    SecByteBlock IVecB(reinterpret_cast<const byte*>(&IvVec[0]), IvVec.size());

    std::string start = "DemonstrationTxt"; //String to encrypt and decrypt
    std::string cipher, plain; //setting up the variable for the cipher and recovered plain text

    //Printing out the Starting Phrase
    std::cout << "Starting Phrase text: " << start << std::endl;

    //Setting up the Instances of Encryption
    CBC_Mode<AES>::Encryption e;
    e.SetKeyWithIV(EncryptKey, EncryptKey.size(), IVecB);

    //Doing the Encryption - Only for demonstration purposes, will be taken out later.
    //Done using Piplining - Takes the data and passes it through the encrytption filter using strings and pointers.
    StringSource s(
        start,
        true,
        new StreamTransformationFilter(e,
            //new Base64Encoder(
                new StringSink(cipher)
            //),
            , StreamTransformationFilter::NO_PADDING
        )
    );

    //Outputting the Cipher Text
    std::cout << "Cipher Text: ";
    encoder.Put((const byte*)&cipher[0], cipher.size()); //Using hex encoding to turn it into a readable string
    encoder.MessageEnd();
    std::cout << std::endl;

    //Doing the Decryption
    //Setting up the instances
    CBC_Mode <AES>::Decryption d;
    d.SetKeyWithIV(EncryptKey, EncryptKey.size(), IVecB);

    //Doing the decryption with piplining
    StringSource ss(
        cipher,
        true,
        //new Base64Decoder(
            new StreamTransformationFilter(d,
                new StringSink(plain),
                StreamTransformationFilter::NO_PADDING
            )
        //)
    );

    std::cout << "Plain Text: " << plain << std::endl;
    */

    /////////////////////////////////////// SHA ///////////////////////////////////////
    std::string DDS;

    CryptoPP::HexEncoder encoder(new CryptoPP::StringSink(DDS)); //encoder to turn bytes into encoded hex

    std::cout << std::endl << "SHA-256 " << std::endl;

    //Setting up the starting message and the output variable
    std::string msgStart = "FinalPresentationDemo";
    std::string msgDigest;

    CryptoPP::SHA256 hashAlgorithm;
    hashAlgorithm.Update((const CryptoPP::byte*)msgStart.data(), msgStart.size()); //adds the data to the given hash
    msgDigest.resize(hashAlgorithm.DigestSize()); //resizes the string to the correct size - in this case 256 bits
    hashAlgorithm.Final((CryptoPP::byte*)&msgDigest[0]); //does the SHA hash algorithum using a pointer

    std::cout << "Starting Text: " << msgStart << std::endl;

    std::cout << "Final Dynamic Digital Signature Text: ";
    CryptoPP::StringSource( //Turning the final binary value into a string
        msgDigest,
        true,
        new CryptoPP::Redirector(encoder)); 
    
    std::cout << DDS << std::endl;

    return 0;
}