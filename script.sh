#!/bin/bash

TIMEOUT=1

get_node_ips() {
    local context="$1"
    local node_ips
    node_ips=$(timeout $TIMEOUT kubectl config use-context "$context" > /dev/null 2>&1 &&
               timeout $TIMEOUT kubectl get nodes -o wide --no-headers | awk '{print $6}')
    echo "$node_ips"
}

main() {

    local active_contexts
    active_contexts=$(kubectl config get-contexts -o name)

    for context in $active_contexts; do
        echo "$context"
        local node_ips
        node_ips=$(get_node_ips "$context")

        if [ -n "$node_ips" ]; then
            echo "$node_ips"
        else
            echo "Error: Failed to get node IPs for context $context within $TIMEOUT seconds. Skipping..."
        fi
        echo
    done
}
main
