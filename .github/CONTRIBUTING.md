# Contributions guide

Thanks for contributing to LoginTo, please follow those simple rules
---
# Commits and Pull Requests

We use **Conventional Commits**. Commits should look like this:

- `feat: ...` for a new feature.
- `fix: ...` for a bug fix.
- `refactor: ...` for changes that don't modify the behavior of the plugin.
---
# Changelog Guidelines

To add clear changelogs for every version, follow these simple steps in `.github/CHANGELOG.md`:

1. Add a horizontal separator (`---`) at the end to separate your change from the previous ones.
2. Write your change using conventional commit format, for example: `**fix: resolve a database table problem**`.
3. If you want to receive credits in the Modrinth changelog, add `@YourUsername` under the commit description (replace `YourUsername` with your GitHub handle).
---
# Pull Requests
1. Create a branch for your changes (e.g., `fix/bug-name` or `feat/feature-name`).
2. Make sure your code compiles without errors (run the `clean` task and recompile).
3. Describe what your pull request does and what problem it solves.