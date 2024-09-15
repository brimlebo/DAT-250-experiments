<script lang="ts">
    import { currentUser } from '$lib/stores/userStore';

    let username: string = '';
    let email: string = '';

    let errorMessage: string = '';
    let successMessage: string = '';

    const createUser = async () => {
        try {
            const response = await fetch('http://localhost:8080/pollApi/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({ username, email }),
            });

            if (response.ok) {
                let user = await response.json();
                currentUser.set(user)
                successMessage = 'User created successfully!';
                errorMessage = '';
            }
            else {
                const errorData = await response.text();
                errorMessage = `Failed to create user: ${errorData}`;
            }
        }
        catch (error) {
            if (error instanceof Error) {
                errorMessage = `An error occurred: ${error.message}`;
            }
            else {
                errorMessage = 'An unknown error occurred.';
            }
        }
    };
</script>

<main>
    <form on:submit|preventDefault={createUser}>
        <label for="username">Username:</label>
        <input type="text" id="username" bind:value={username} placeholder="Enter username" required />

        <label for="email">Email:</label>
        <input type="email" id="email" bind:value={email} placeholder="Enter email" required />

        <button type="submit">Create User</button>
    </form>

    {#if errorMessage}
        <p style="color: red;">{errorMessage}</p>
    {/if}

    {#if successMessage}
        <p style="color: green;">{successMessage}</p>
    {/if}
</main>