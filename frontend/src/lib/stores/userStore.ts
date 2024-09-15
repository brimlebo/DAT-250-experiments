// src/lib/stores/userStore.ts
import { writable } from 'svelte/store';

// Helper function to load the user from localStorage
function loadUserFromStorage() {
    if (typeof localStorage !== 'undefined') {
        const storedUser = localStorage.getItem('currentUser');
        if (storedUser) {
            return JSON.parse(storedUser);
        }
    }
    return { userID: -1, username: '', email: '' };
}

// Create a writable store for the current user
const currentUser = writable(loadUserFromStorage());

// Subscribe to changes and persist the user to localStorage
currentUser.subscribe((user) => {
    if (typeof localStorage !== 'undefined') {
        if (user.userID === -1) {
            localStorage.removeItem('currentUser'); // Clear storage on logout
        } else {
            localStorage.setItem('currentUser', JSON.stringify(user)); // Save user to storage
        }
    }
});

// Export the store
export { currentUser };