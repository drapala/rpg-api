````markdown
# 🔄 Development Workflow

This project follows a simple **feature branch workflow** to keep changes isolated and the `main` branch clean.

## 1. Create a feature branch
Each new feature or fix should start in its own branch:
```sh
git checkout -b feature/character-creation
````

## 2. Commit your changes

Make small, meaningful commits as you progress:

```sh
git add .
git commit -m "Implement character creation endpoint with validation"
```

## 3. Keep `main` clean

When the feature is complete, switch back to `main` and merge:

```sh
git checkout main
git merge feature/character-creation
```

## 4. Open a Pull Request (PR)

Even if working solo, treat each feature branch as if you were collaborating:

* Open a PR from `feature/...` → `main`
* Review changes
* Merge after approval (or self-review in this case)

## 5. Run tests before merging

```sh
./mvnw test
```

## 6. Submission (Neo instructions)

Once all features are merged into `main`, generate the bundle for submission:

```sh
git bundle create submission-<your-name>.bundle main
```

---

✅ This workflow demonstrates **professional Git practices** (branch per feature, PR, clean main branch) while remaining simple enough for a take-home assignment.

```
