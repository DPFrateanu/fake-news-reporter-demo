# Contributing to Fake News Reporter

Thank you for your interest in contributing to Fake News Reporter! This guide will help you get started with forking the repository and making contributions.

## Table of Contents

- [Getting Started](#getting-started)
- [How to Fork and Clone](#how-to-fork-and-clone)
- [Setting Up Your Development Environment](#setting-up-your-development-environment)
- [Creating a Branch](#creating-a-branch)
- [Making Changes](#making-changes)
- [Submitting a Pull Request](#submitting-a-pull-request)
- [Keeping Your Fork Up to Date](#keeping-your-fork-up-to-date)
- [Coding Standards](#coding-standards)
- [Commit Message Guidelines](#commit-message-guidelines)

## Getting Started

Before you begin contributing, make sure you have:

- A GitHub account
- Git installed on your local machine
- Java 17 or higher installed
- Maven 3.6+ installed
- Basic familiarity with Spring Boot applications

## How to Fork and Clone

### Step 1: Fork the Repository

1. Navigate to the [Fake News Reporter repository](https://github.com/automatica-cluj/demo-project) on GitHub
2. Click the **Fork** button in the top-right corner of the page
3. Select your GitHub account as the destination for the fork
4. Wait for GitHub to create your fork (this usually takes a few seconds)

### Step 2: Clone Your Fork

After forking, clone your fork to your local machine:

```bash
# Replace YOUR-USERNAME with your actual GitHub username
git clone https://github.com/YOUR-USERNAME/demo-project.git

# Navigate into the project directory
cd demo-project
```

### Step 3: Add Upstream Remote

To keep your fork synchronized with the original repository, add it as an upstream remote:

```bash
# Add the original repository as "upstream"
git remote add upstream https://github.com/automatica-cluj/demo-project.git

# Verify the remotes
git remote -v
```

You should see output like:

```
origin    https://github.com/YOUR-USERNAME/demo-project.git (fetch)
origin    https://github.com/YOUR-USERNAME/demo-project.git (push)
upstream  https://github.com/automatica-cluj/demo-project.git (fetch)
upstream  https://github.com/automatica-cluj/demo-project.git (push)
```

## Setting Up Your Development Environment

1. **Verify Java version:**
   ```bash
   java -version
   # Should be Java 17 or higher
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application:**
   - Open http://localhost:8080 in your browser
   - Login with username: `admin`, password: `admin123`

## Creating a Branch

Always create a new branch for your changes. Never commit directly to the `main` branch.

### Branch Naming Conventions

Use descriptive branch names that indicate the type of change:

- `feature/description` - For new features
- `fix/description` - For bug fixes
- `docs/description` - For documentation changes
- `refactor/description` - For code refactoring
- `test/description` - For adding or updating tests

### Creating Your Branch

```bash
# Make sure you're on the main branch
git checkout main

# Pull the latest changes from upstream
git pull upstream main

# Create and switch to your new branch
git checkout -b feature/your-feature-name

# Or use the combined command
git checkout -b fix/your-bug-fix
```

### Examples

```bash
# Adding a new feature
git checkout -b feature/add-report-search

# Fixing a bug
git checkout -b fix/report-approval-error

# Updating documentation
git checkout -b docs/update-readme

# Refactoring code
git checkout -b refactor/simplify-auth
```

## Making Changes

1. **Make your changes** to the code, following the [Coding Standards](#coding-standards)

2. **Test your changes:**
   ```bash
   # Run all tests
   mvn test

   # Run the application and test manually
   mvn spring-boot:run
   ```

3. **Stage your changes:**
   ```bash
   # Add specific files
   git add src/main/java/com/automatica/fakenews/YourFile.java

   # Or add all changed files
   git add .
   ```

4. **Commit your changes** using [conventional commit messages](#commit-message-guidelines):
   ```bash
   git commit -m "feat: add search functionality to reports page"
   ```

## Submitting a Pull Request

### Step 1: Push Your Branch

```bash
# Push your branch to your fork on GitHub
git push origin feature/your-feature-name
```

### Step 2: Create the Pull Request

1. Go to your fork on GitHub (https://github.com/YOUR-USERNAME/demo-project)
2. You'll see a banner suggesting to create a pull request - click **Compare & pull request**
3. Alternatively, go to the **Pull requests** tab and click **New pull request**

### Step 3: Fill Out the PR Details

- **Title**: Use a clear, descriptive title following conventional commit format
  - Examples: `feat: add report search`, `fix: resolve login timeout`
- **Description**: Provide details about your changes:
  - What problem does this solve?
  - What changes did you make?
  - How can reviewers test your changes?
  - Are there any breaking changes?
  - Screenshots (if applicable)

### Step 4: Wait for Review

- Maintainers will review your pull request
- They may request changes or ask questions
- Make any requested changes and push them to your branch
- The PR will automatically update with your new commits

### Step 5: After Approval

Once approved, a maintainer will merge your pull request. You can then:

1. Delete your branch on GitHub (button appears after merge)
2. Delete your local branch:
   ```bash
   git checkout main
   git branch -d feature/your-feature-name
   ```

## Keeping Your Fork Up to Date

Regularly sync your fork with the upstream repository:

```bash
# Switch to your main branch
git checkout main

# Fetch changes from upstream
git fetch upstream

# Merge upstream changes into your main branch
git merge upstream/main

# Push the updates to your fork
git push origin main
```

### Updating Your Feature Branch

If your feature branch becomes outdated while you're working:

```bash
# Switch to your feature branch
git checkout feature/your-feature-name

# Fetch latest changes from upstream
git fetch upstream

# Rebase your branch onto the latest main
git rebase upstream/main

# If there are conflicts, resolve them and continue:
# git add <resolved-files>
# git rebase --continue

# Force push to your fork (only for feature branches, never for main!)
git push origin feature/your-feature-name --force
```

## Coding Standards

### Java Code Style

- Follow standard Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Keep methods small and focused
- Use Spring Boot best practices

### Code Organization

```java
// Example structure
package com.automatica.fakenews.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling report submissions.
 */
@Controller
public class ReportController {
    
    private final ReportService reportService;
    
    // Constructor injection preferred
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    // Methods here
}
```

### Testing

- Write tests for new features
- Ensure existing tests pass
- Test both success and failure cases
- Use meaningful test names

```java
@Test
void shouldApproveReportSuccessfully() {
    // Test implementation
}
```

## Commit Message Guidelines

This project follows [Conventional Commits](https://www.conventionalcommits.org/) specification.

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat:` - New feature (triggers minor version bump)
- `fix:` - Bug fix (triggers patch version bump)
- `docs:` - Documentation changes (no version bump)
- `style:` - Code style changes (formatting, missing semicolons, etc.)
- `refactor:` - Code refactoring (no functional changes)
- `test:` - Adding or updating tests
- `chore:` - Maintenance tasks (updating dependencies, etc.)
- `perf:` - Performance improvements
- `ci:` - CI/CD changes

### Examples

```bash
# New feature
git commit -m "feat: add pagination to reports list"

# Bug fix
git commit -m "fix: resolve report approval timestamp issue"

# Documentation
git commit -m "docs: update contributing guide"

# With scope
git commit -m "feat(auth): add OAuth2 support"

# With body
git commit -m "feat: add report filtering

- Add category filter dropdown
- Add date range filter
- Add URL search functionality"

# Breaking change
git commit -m "feat!: redesign admin API

BREAKING CHANGE: Admin endpoints now require authentication header
instead of session cookies."
```

### Commit Best Practices

- Write clear, descriptive commit messages
- Keep commits focused on a single change
- Commit early and often during development
- Use the body to explain *why*, not *what*
- Reference issue numbers when applicable: `fix: resolve #123`

## Questions or Problems?

If you have questions or run into issues:

1. Check the [README.md](README.md) for basic setup instructions
2. Review the [CI/CD Guide](.github/CI_CD_GUIDE.md) for workflow information
3. Search existing [GitHub Issues](https://github.com/automatica-cluj/demo-project/issues)
4. Create a new issue with:
   - Clear description of the problem
   - Steps to reproduce (if applicable)
   - Your environment (OS, Java version, etc.)
   - Any error messages or logs

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on the code, not the person
- Help others learn and grow
- Follow the project's guidelines

Thank you for contributing to Fake News Reporter! 🎉
