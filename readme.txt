Dependencies:

- JDK 1.8

External Dependencies:

- Apache Commons CLI Version 1.3.1
- Apache Commons IO Version 2.5
- Apache Commons Lang Version 3.4

Computer Specifications:

OS Name	                                                Microsoft Windows 10 Pro Insider Preview
Version	                                                10.0.14328 Build 14328
Other OS Description 	                                Not Available
OS Manufacturer	                                        Microsoft Corporation
System Name	                                            DESKTOP-77GT2RA
System Manufacturer	                                    System manufacturer
System Model	                                        System Product Name
System Type	                                            x64-based PC
System SKU	                                            SKU
Processor	                                            Intel(R) Core(TM) i5-3570K CPU @ 3.40GHz, 3401 Mhz, 4 Core(s), 4 Logical Processor(s)
BIOS Version/Date	                                    American Megatrends Inc. 1708, 09/11/2012
SMBIOS Version	                                        2.7
Embedded Controller Version	                            255.255
BIOS Mode	                                            UEFI
BaseBoard Manufacturer	                                ASUSTeK COMPUTER INC.
BaseBoard Model	                                        Not Available
BaseBoard Name	                                        Base Board
Platform Role	                                        Desktop
Secure Boot State	                                    On
PCR7 Configuration	                                    Binding Not Possible
Windows Directory	                                    C:\WINDOWS
System Directory	                                    C:\WINDOWS\system32
Boot Device	                                            \Device\HarddiskVolume4
Locale	                                                United Kingdom
Hardware Abstraction Layer	                            Version = "10.0.14328.1000"
Username	                                            DESKTOP-77GT2RA\timsw
Time Zone	                                            Romance Summer Time
Installed Physical Memory (RAM)	                        8.00 GB
Total Physical Memory	                                7.94 GB
Available Physical Memory	                            1.71 GB
Total Virtual Memory	                                13.9 GB
Available Virtual Memory	                            2.20 GB
Page File Space	                                        5.91 GB
Page File	                                            C:\pagefile.sys
Hyper-V - VM Monitor Mode Extensions	                Yes
Hyper-V - Second Level Address Translation Extensions	Yes
Hyper-V - Virtualisation Enabled in Firmware	        No
Hyper-V - Data Execution Protection	                    Yes

Performance:

- Hashing
    Try to reuse MessageDigest instance as much as possible by resetting existing ones.

Future improvements:

-   Separate client and vault in separate processes. (Client does not have interaction with own vault anyway)
    The vault should be accessible at all times when machine is running. (Run as a background process)
-   Further try to reuse the MessageDigest instances.

Conventions in code functionality:

-   Peers return null when the file could not be located.