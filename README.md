# CaptivePortalUniv
Tired of typing my credentials every time I needed to use my university's WIFI, I decided to make use of the Android security features such as the Keystore and the [ConfirmCredential API](https://github.com/googlesamples/android-ConfirmCredential) in order to help me smoothly login.

## Features
  - [x] Stores the symmetric keys into the Android Keystore
  - [x] Uses ConfirmCredential API for encrypting|decrypting the credentials
  - [x] Auto connect if logged less than 5 seconds ago
  - [ ] Implement Certificate Pining to strengthen security again MIT attack
