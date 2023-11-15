#include <iostream>
#include <string>

int main(){
    //Getting the command information from the device (DDS, Command, Timestamp)
    std::cout << "Enter DDS and Command from Vehicle: ";
    std::string InfoDevice;
    std::cin >> InfoDevice;

    //Setting up the Vehicle Stored DDS Value (for testing)
    std::string DDSVehicle;
    DDSVehicle = "C0mmun1c8t10nAndCr7ptoSubsystem2";

    //Striping out the DDS from the device information
    std::string DDSDevice;
    DDSDevice = InfoDevice.substr(0,32);

    //Verification that the DDSs match
    if (DDSDevice == DDSVehicle){
        std::cout << "DDSs match: Send Command to Vehicle.\n";
    } else {
        std::cout << "DDSs do not match: No further action.\n";
    }

}