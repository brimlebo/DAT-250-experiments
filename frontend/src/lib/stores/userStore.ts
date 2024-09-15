import { writable } from 'svelte/store';

// Define the type for the user
export interface User {
    userID: number;
    username: string;
    email: string;
}

// Create a writable store with a default value
export const currentUser = writable<User>({ userID: -1, username: "no", email: "u" });