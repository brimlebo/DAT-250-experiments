<script lang="ts">
    import { currentUser } from '$lib/stores/userStore';

    let username = '';
    let email = '';
    let errorMessage = '';
    let successMessage = '';
    let isLogin = true; // true = login mode, false = register mode

    // Function to register a new user
    const registerUser = async () => {
        try {
            const response = await fetch(`http://localhost:8080/pollApi/users?username=${encodeURIComponent(username)}&email=${encodeURIComponent(email)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (response.ok) {
                const newUser = await response.json();
                successMessage = `User ${newUser.username} created successfully!`;
                errorMessage = '';
                currentUser.set(newUser); // Set new user in the store
            } else {
                const errorData = await response.text();
                errorMessage = `Failed to create user: ${errorData}`;
                successMessage = '';
            }
        } catch (error: unknown) {
            errorMessage = 'An error occurred while creating the user.';
            successMessage = '';
        }
    };

    // Function to log in a user
    const loginUser = async () => {
        try {
            const response = await fetch('http://localhost:8080/pollApi/users');
            if (response.ok) {
                const users = await response.json();
                const existingUser = users.find((user: { id: number, username: string, email: string }) => user.username === username && user.email === email);

                if (existingUser) {
                    successMessage = `Welcome back, ${existingUser.username}!`;
                    errorMessage = '';
                    currentUser.set(existingUser); // Set logged-in user in the store
                } else {
                    errorMessage = 'No user found with the provided credentials.';
                    successMessage = '';
                }
            } else {
                errorMessage = 'Failed to fetch users for login.';
                successMessage = '';
            }
        } catch (error: unknown) {
            errorMessage = 'An error occurred during login.';
            successMessage = '';
        }
    };

    // Toggle between login and registration modes
    const toggleMode = () => {
        isLogin = !isLogin;
        successMessage = '';
        errorMessage = '';
    };

    // Handle form submission based on mode (login/register)
    const handleSubmit = async () => {
        if (isLogin) {
            await loginUser();
        } else {
            await registerUser();
        }
    };
</script>

<main>
    <h2>{isLogin ? 'Login' : 'Create a New Account'}</h2>

    <form on:submit|preventDefault={handleSubmit}>
        <label for="username">Username:</label>
        <input type="text" id="username" bind:value={username} placeholder="Enter username" required />

        <label for="email">Email:</label>
        <input type="email" id="email" bind:value={email} placeholder="Enter email" required />

        <button type="submit">{isLogin ? 'Login' : 'Register'}</button>
    </form>

    <button on:click={toggleMode}>
        {isLogin ? 'Create a new account' : 'Back to login'}
    </button>

    {#if errorMessage}
        <p style="color: red;">{errorMessage}</p>
    {/if}

    {#if successMessage}
        <p style="color: green;">{successMessage}</p>
    {/if}
</main>

<style>
    main {
        padding: 1rem;
    }
    label {
        display: block;
        margin-top: 1rem;
    }
    input {
        display: block;
        margin-bottom: 1rem;
        padding: 0.5rem;
        width: 100%;
    }
    button {
        margin-top: 1rem;
    }
</style>