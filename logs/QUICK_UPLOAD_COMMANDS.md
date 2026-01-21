# Quick Upload Commands - Copy Paste Karo

## 🚀 GitHub Par Upload Karne Ke Liye Ye Commands Run Karein

---

## 📝 COMPLETE COMMAND SEQUENCE

**PowerShell ya Command Prompt mein ye commands run karein:**

```powershell
# Step 1: Project folder mein jao
cd I:\FAWAD\basic\p

# Step 2: Git initialize
git init

# Step 3: Sab files add karo
git add .

# Step 4: Commit karo
git commit -m "Initial commit - LinkUp project"

# Step 5: GitHub repository se connect karo
git remote add origin https://github.com/Amaan-Masood-0086/APK.git

# Step 6: Main branch set karo
git branch -M main

# Step 7: Push karo (ye command authentication maangega)
git push -u origin main
```

---

## 🔐 AUTHENTICATION

**Step 7 mein jab `git push` run karein:**

1. **Username:** Apna GitHub username enter karein
2. **Password:** 
   - Normal password nahi chalega
   - **Personal Access Token** chahiye

### **Personal Access Token Kaise Banayein:**

1. GitHub.com par jayein
2. **Settings** → **Developer settings** → **Personal access tokens** → **Tokens (classic)**
3. **Generate new token (classic)** click karein
4. **Note:** "APK Build" (ya kuch bhi)
5. **Expiration:** 90 days (ya jitna chahiye)
6. **Scopes:** `repo` checkbox select karein
7. **Generate token** click karein
8. **Token copy karein** (sirf ek baar dikhega!)

**Push command mein:**
- Username: Apna username
- Password: Token paste karein (password ki jagah)

---

## ⚡ ONE-LINER (Agar Git Already Configured Hai)

```powershell
cd I:\FAWAD\basic\p; git init; git add .; git commit -m "Initial commit"; git remote add origin https://github.com/Amaan-Masood-0086/APK.git; git branch -M main; git push -u origin main
```

---

## ✅ AFTER PUSH

1. **GitHub repository** refresh karein
2. **Actions** tab check karein
3. **Build automatically start** hoga
4. **APK download** karein (3-5 minutes baad)

---

## 🎯 STATUS CHECK

**Check karne ke liye:**
```powershell
git status
git remote -v
```

---

**Ready to upload!** Commands copy karke run karein.
