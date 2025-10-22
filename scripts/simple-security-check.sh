#!/bin/bash

# Simple dependency security check script
# This runs when other security tools fail

echo "🔍 Running simplified security check..."

# Get all dependencies
echo "📦 Analyzing project dependencies..."
./mvnw dependency:list -DoutputFile=dependencies.txt -Dsilent=true

if [ -f dependencies.txt ]; then
    echo "✅ Dependencies list generated successfully"
    
    # Check for known problematic versions
    echo "🔎 Checking for known vulnerable dependency patterns..."
    
    VULNERABLE_PATTERNS=(
        "log4j-core:2\.1[0-6]\."
        "spring-core:[0-4]\."
        "jackson-databind:2\.[0-9]\.[0-7]"
        "spring-boot:[0-1]\."
    )
    
    FOUND_ISSUES=0
    
    for pattern in "${VULNERABLE_PATTERNS[@]}"; do
        if grep -E "$pattern" dependencies.txt > /dev/null; then
            echo "⚠️  Found potentially vulnerable dependency matching pattern: $pattern"
            FOUND_ISSUES=$((FOUND_ISSUES + 1))
        fi
    done
    
    if [ $FOUND_ISSUES -eq 0 ]; then
        echo "✅ No known vulnerable dependency patterns found"
    else
        echo "⚠️  Found $FOUND_ISSUES potential security concerns"
        echo "💡 Consider updating dependencies or running a full security scan"
    fi
    
    # Clean up
    rm -f dependencies.txt
    
else
    echo "❌ Could not generate dependencies list"
    exit 1
fi

echo "🏁 Simplified security check completed"