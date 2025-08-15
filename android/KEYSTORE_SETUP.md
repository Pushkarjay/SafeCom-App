# 🔑 Keystore Setup Instructions

This file contains instructions for setting up the signing keystore for SafeCom app.

## 🚨 IMPORTANT SECURITY NOTICE

The actual keystore file (`safecom-release-key.keystore`) is **NOT** included in this repository for security reasons. You need to:

1. **Generate a new keystore** OR
2. **Use the existing keystore** (if you have it)

## 🔧 Generate New Keystore

If you need to create a new keystore, run this command in the `android` directory:

```bash
# Navigate to android directory
cd android

# Set JAVA_HOME (adjust path as needed)
set JAVA_HOME=D:\JDK  # Windows
export JAVA_HOME=/path/to/jdk  # macOS/Linux

# Generate keystore
$JAVA_HOME/bin/keytool -genkeypair -v -keystore safecom-release-key.keystore -alias safecom -keyalg RSA -keysize 2048 -validity 10000 -storepass "YOUR_PASSWORD" -keypass "YOUR_PASSWORD" -dname "CN=SafeCom App, OU=Development, O=SafeCom, L=City, ST=State, C=US"
```

## 📋 Required Information

When creating the keystore, you'll need:

- **Keystore Password**: Choose a strong password
- **Key Alias**: `safecom` (already configured)
- **Key Password**: Use same as keystore password
- **Validity**: 10,000 days (~27 years)

## 🔧 Update Build Configuration

After creating the keystore, update the password in `android/app/build.gradle`:

```gradle
signingConfigs {
    release {
        storeFile file('../safecom-release-key.keystore')
        storePassword 'YOUR_PASSWORD_HERE'
        keyAlias 'safecom'
        keyPassword 'YOUR_PASSWORD_HERE'
    }
}
```

## 🔒 Security Best Practices

1. **NEVER** commit keystore files to version control
2. **ALWAYS** backup keystores securely
3. **REMEMBER** passwords - they cannot be recovered
4. **RESTRICT** access to keystore files
5. **USE** environment variables for passwords in CI/CD

## 📁 Expected Location

Place your keystore file at:
```
SafeCom-App/
└── android/
    └── safecom-release-key.keystore  ← Place keystore here
```

## 🆘 Need Help?

If you're the original developer (Pushkarjay), you should have the keystore backed up securely. If you're a new developer, you'll need to generate a new keystore for your builds.

**Note**: Using a different keystore will create a different app signature, so users will need to uninstall the previous version before installing the new one.
