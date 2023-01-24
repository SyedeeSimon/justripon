import React, {useCallback, useEffect, useState} from "react";

const X_CURRENT_USER_NAME = "X-CurrentUserName";
const X_CURRENT_USER_ID = "X-CurrentUserId";
const AUTHORIZATION = "Authorization";



export const useCurrentUser = () => {
    const [userName, setUserName] = useState(null);
    const [userId, setUserId] = useState(null);
    const [token, setToken] = useState(null);

    const saveHeadersInStorage = (response: Response) => {
        window.sessionStorage.setItem(AUTHORIZATION, response.headers.get(AUTHORIZATION));
        window.sessionStorage.setItem(X_CURRENT_USER_NAME, response.headers.get(X_CURRENT_USER_NAME));
        window.sessionStorage.setItem(X_CURRENT_USER_ID, response.headers.get(X_CURRENT_USER_ID));
        
        setUserName(response.headers.get(X_CURRENT_USER_NAME));
        setUserId(response.headers.get(X_CURRENT_USER_ID));
    }

    useEffect(()=>{
        const performAnonymousLogin = async () => {
            const response = await fetch('http://localhost/api/rest/auth/basic/anonymous-login', {
                method: 'GET'
            });
            if(response.status === 202) {
                saveHeadersInStorage(response);
            }            
        }
        const verifyTokenAsync = async (token: string) => {
            const response = await fetch('http://localhost:8080/auth/basic/login', {
                method: 'GET',
                headers: {
                    [AUTHORIZATION]: token
                }
            });
            if (response.status === 202) {
                saveHeadersInStorage(response);
                return;
            }
            performAnonymousLogin();
        };
        performAnonymousLogin();
    }, []);

    return [userName, userId];
};
