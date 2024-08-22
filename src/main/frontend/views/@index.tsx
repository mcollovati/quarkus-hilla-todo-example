import {Button, Checkbox, TextField} from "@vaadin/react-components";
import {useForm} from '@vaadin/hilla-react-form';
import {useEffect, useState} from "react";
import Todo from "Frontend/generated/com/example/application/Todo";
import TodoModel from "Frontend/generated/com/example/application/TodoModel";
import {TodoEndpoint} from "Frontend/generated/endpoints";

export default function TodoView() {
    const [todos, setTodos] = useState<Todo[]>([]);
    const {
        model,
        field,
        submit,
        clear,
        invalid,
        submitting
    } = useForm(TodoModel, {
        onSubmit: async (todo) => {
            let newItem = await TodoEndpoint.create(todo);
            setTodos([...todos, newItem]);
            clear();
        }
    });
    const updateTodoState = (todo: Todo, done: boolean) => {
        if (todo.done !== done) {
            todo.done = done;
            TodoEndpoint.update(todo).then(updated => {
                setTodos(todos.map(t => {
                    if (t.id === updated?.id) {
                        return updated;
                    }
                    return t;
                }));
            });
        }
    };

    useEffect(() => {
        TodoEndpoint.findAll().then(setTodos)
    }, []);

    return (
        <>
            <div className="form">
                <TextField label="Task"
                           {...field(model.task)}></TextField>
                <Button theme="primary" disabled={invalid || submitting}
                        onClick={submit}>
                    Add
                </Button>
            </div>
            <div className="todos">
                {todos.map(
                    (todo) => (
                        <div key={todo.id} className="todo">
                            <Checkbox
                                checked={todo.done}
                                onCheckedChanged={(e: CustomEvent) => updateTodoState(todo, e.detail.value)}></Checkbox>
                            <span>{todo.task}</span>
                        </div>
                    ))}
            </div>
        </>
    );
}