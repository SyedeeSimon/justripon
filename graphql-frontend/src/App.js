// @flow

import React, {Suspense, type Node} from "react";

import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';

import UserPage from './user-page';
import AdminPage from './admin-page';

import {InitialPageSize} from 'commons/constants';

import {Route} from 'react-router-dom';

import Home from './home/index';
import Exam from './exam/index';

import './App.css';

import {
    RelayEnvironmentProvider,
    loadQuery,
    usePreloadedQuery,
} from 'react-relay';

import RelayEnvironment from 'relay-environment/RelayEnvironment';

import Scaffolding from "app-scaffolding/index";
// import basicLogin from './anonymous-login/index';

import {useCurrentUser} from 'user-authentication/index';


const App = () => {
    const [userName, userId] = useCurrentUser();

    console.log('userName = ', userName);
    console.log('userId = ', userId);


    return (<div>
        <Route path={"/home"}>
            <Home/>
        </Route>
        <Route path={"/exam"}>
            <Exam/>
        </Route>
    </div>);
}

const AppRoot = (): Node => {

    return <RelayEnvironmentProvider environment={RelayEnvironment}>
        <Suspense fallback={
            <Backdrop sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}}
                      open={open}
            >
                <CircularProgress color="inherit"/>
            </Backdrop>
        }>
            <App/>
        </Suspense>
    </RelayEnvironmentProvider>;
};

export default AppRoot;