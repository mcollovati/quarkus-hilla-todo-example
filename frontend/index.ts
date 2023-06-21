import { Router } from '@vaadin/router';
import './todo-view'

export const router = new Router(document.querySelector('#outlet'));
const routes = [
    {
        path: '',
        component: 'todo-view',
    },
]

router.setRoutes(routes);