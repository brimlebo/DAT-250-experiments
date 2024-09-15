<script lang="ts">
    import CreatePoll from '$lib/components/CreatePoll.svelte';
    import VoteOnPoll from '$lib/components/VoteOnPoll.svelte';
    import CreateUser from '$lib/components/CreateUser.svelte';

    import { page } from '$app/stores';
    import { onMount } from 'svelte';
    import { currentUser } from '$lib/stores/userStore';

    let pollID: number;
    let polls: Array<{
        pollID: number;
        question: string;
        publishedAt: string;
        validUntil: string;
        creator: { userID: number, username: string, email: string };
        voteOptions: string[];
    }> = [];

    let errorMessage: string = '';

    const logout = () => {
        currentUser.set({ userID: -1, username: '', email: '' }); // Reset the user
    };

    // Fetch the list of polls
    const fetchPolls = async () => {
        try {
            const response = await fetch('http://localhost:8080/pollApi/polls');
            if (response.ok) {
                polls = await response.json();
            } else {
                errorMessage = 'Failed to fetch polls';
            }
        } catch (error: unknown) {
            if (error instanceof Error) {
                errorMessage = `An error occurred while fetching polls: ${error.message}`;
            } else {
                errorMessage = 'An unknown error occurred while fetching polls.';
            }
        }
    };

    // This will be called when a new poll is successfully created
    const addPollToList = (newPoll: {
        pollID: number;
        question: string;
        publishedAt: string;
        validUntil: string;
        creator: { userID: number, username: string, email: string };
        voteOptions: string[];
    }) => {
        polls = [...polls, newPoll]; // Add the new poll to the list of polls
    };

    $: pollID = $page.url.searchParams.get('pollID') ? Number($page.url.searchParams.get('pollID')) : 9843;

    onMount(() => {
        fetchPolls();
    });
</script>

<main>
    {#if $currentUser.userID === -1}
        <h1>Please make a user</h1>

        <!-- Create User Section -->
        <section id="create-user">
            <CreateUser />
        </section>
    {:else}
        <h1>Welcome to the Polling App, {$currentUser.username}</h1>

        <button on:click={logout}>Log Out</button>

         <!-- Create Poll Section -->
        <section id="create-poll">
            <h2>Create Poll</h2>
            <CreatePoll {addPollToList} />
        </section>

        <!-- Vote on Poll Section -->
        <section id="vote-on-poll">
            <h2>Vote on Poll</h2>
            {#if pollID !== 9843}
                <VoteOnPoll {pollID} />
            {:else}
                <p>Select a poll to vote on:</p>

                <!-- Display the list of polls -->
                {#if polls.length > 0}
                    <ul>
                        {#each polls as poll}
                            <li>
                                <a href={`?pollID=${poll.pollID}`}>{poll.question}</a>
                            </li>
                        {/each}
                    </ul>
                {:else}
                    <p>No polls available at the moment.</p>
                {/if}
            {/if}

            {#if errorMessage}
                <p style="color: red;">{errorMessage}</p>
            {/if}
        </section>
    {/if}
</main>

<style>
    main {
        padding: 2rem;
    }

    section {
        margin-top: 2rem;
        padding: 1rem;
        border: 1px solid #ddd;
    }

    ul {
        list-style: none;
        padding: 0;
    }

    li {
        margin: 0.5rem 0;
    }

    a {
        text-decoration: none;
        color: blue;
    }

    a:hover {
        text-decoration: underline;
    }
</style>