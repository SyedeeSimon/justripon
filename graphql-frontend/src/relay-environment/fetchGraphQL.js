async function fetchGraphQL(text, variables) {
    await basicLogin();
    console.log("text ", text);
    console.log('variables  ', variables);
    const response = await fetch('http://localhost:8080/graphql', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            query: text,
            variables,
        }),
    });
    return await response.json();
}

async function basicLogin(){
    console.log('running basic login')
    await fetch('http://localhost:8080/auth/basic/anonymous-login', {
        method: 'GET',
        headers: {
            'Authorization': 'Basic YW5vbnltb3VzX3VzZXI6YW5vbnltb3VzX3VzZXJfcGFzc3dvcmQ='
        }
    });
}

export default fetchGraphQL;