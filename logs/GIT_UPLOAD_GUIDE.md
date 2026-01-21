# Git Command Line - Project Upload Guide

## 🚀 GitHub Par Project Upload Karne Ka Complete Guide

---

## 📋 STEP-BY-STEP COMMANDS

### **Step 1: Git Initialize Karein**

```bash
cd I:\FAWAD\basic\p
git init
```

---

### **Step 2: All Files Add Karein**

```bash
git add .
```

---

### **Step 3: First Commit Karein**

```bash
git commit -m "Initial commit - LinkUp project"
```

---

### **Step 4: GitHub Repository Se Connect Karein**

```bash
git remote add origin https://github.com/Amaan-Masood-0086/APK.git
```

---

### **Step 5: Main Branch Set Karein**

```bash
git branch -M main
```

---

### **Step 6: Code Push Karein**

```bash
git push -u origin main
```

---

## 🔐 AUTHENTICATION

### **Option 1: Personal Access Token (Recommended)**

1. **GitHub Settings:**
   - GitHub.com → Settings → Developer settings → Personal access tokens → Tokens (classic)
   - "Generate new token" click karein
   - Scopes: `repo` select karein
   - Generate karein aur token copy karein

2. **Push Command:**
   ```bash
   git push -u origin main
   ```
   - Username: Apna GitHub username
   - Password: Token paste karein (password ki jagah)

### **Option 2: GitHub CLI**

```bash
# Install GitHub CLI (if not installed)
# Then login
gh auth login
```

---

## 📝 COMPLETE COMMAND SEQUENCE

**Sab commands ek saath:**

```bash
# Navigate to project
cd I:\FAWAD\basic\p

# Initialize git
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit - LinkUp project"

# Add remote
git remote add origin https://github.com/Amaan-Masood-0086/APK.git

# Set main branch
git branch -M main

# Push to GitHub
git push -u origin main
```

---

## ⚠️ IMPORTANT NOTES

### **Before Pushing:**

1. **Check .gitignore:**
   - Ensure build folders ignored hain
   - `.gradle/`, `build/`, `.idea/` ignore hona chahiye

2. **Large Files:**
   - Agar koi large files hain to Git LFS use karein
   - Ya unhein ignore karein

3. **Sensitive Data:**
   - API keys, passwords check karein
   - `.gitignore` mein add karein

---

## 🔍 TROUBLESHOOTING

### Issue: Authentication Failed
**Solution:**
- Personal Access Token use karein
- Password ki jagah token paste karein

### Issue: Remote Already Exists
**Solution:**
```bash
git remote remove origin
git remote add origin https://github.com/Amaan-Masood-0086/APK.git
```

### Issue: Large File Error
**Solution:**
- Large files ko `.gitignore` mein add karein
- Ya Git LFS use karein

---

## ✅ AFTER UPLOAD

### **Workflow File Check:**
- `.github/workflows/build-apk.yml` file upload ho chuki hai
- GitHub Actions automatically trigger hoga

### **Build APK:**
1. GitHub repository par jayein
2. **Actions** tab click karein
3. Build start hoga automatically
4. APK download karein

---

## 🎯 QUICK REFERENCE

```bash
# Complete sequence
cd I:\FAWAD\basic\p
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/Amaan-Masood-0086/APK.git
git branch -M main
git push -u origin main
```

---

**Last Updated:** Current Session
