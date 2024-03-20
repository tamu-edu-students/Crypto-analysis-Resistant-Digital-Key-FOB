# Distributed under the OSI-approved BSD 3-Clause License.  See accompanying
# file Copyright.txt or https://cmake.org/licensing for details.

cmake_minimum_required(VERSION 3.5)

file(MAKE_DIRECTORY
  "C:/Users/agmat/OneDrive/Desktop/esp-idf/components/bootloader/subproject"
  "C:/Users/agmat/esp/bt_spp_acceptor/build/bootloader"
  "C:/Users/agmat/esp/bt_spp_acceptor/build/bootloader-prefix"
  "C:/Users/agmat/esp/bt_spp_acceptor/build/bootloader-prefix/tmp"
  "C:/Users/agmat/esp/bt_spp_acceptor/build/bootloader-prefix/src/bootloader-stamp"
  "C:/Users/agmat/esp/bt_spp_acceptor/build/bootloader-prefix/src"
  "C:/Users/agmat/esp/bt_spp_acceptor/build/bootloader-prefix/src/bootloader-stamp"
)

set(configSubDirs )
foreach(subDir IN LISTS configSubDirs)
    file(MAKE_DIRECTORY "C:/Users/agmat/esp/bt_spp_acceptor/build/bootloader-prefix/src/bootloader-stamp/${subDir}")
endforeach()
if(cfgdir)
  file(MAKE_DIRECTORY "C:/Users/agmat/esp/bt_spp_acceptor/build/bootloader-prefix/src/bootloader-stamp${cfgdir}") # cfgdir has leading slash
endif()
