const form = document.querySelector('#job-form');
const message = document.querySelector('#message');
const formMessage = document.querySelector('#form-message');
const save = document.querySelector('#save');
let editingId = null;
let loadVersion = 0;

async function api(path = '', options = {}) {
    const response = await fetch('/api/jobs' + path, options);
    if (!response.ok) {
        const problem = await response.json().catch(() => ({}));
        const details = problem.errors ? Object.entries(problem.errors).map(([field, error]) => `${field}: ${error}`).join('; ') : problem.detail;
        throw new Error(details || `Request failed (${response.status})`);
    }
    return response.status === 204 ? null : response.json();
}

function element(tag, text, className) {
    const node = document.createElement(tag);
    node.textContent = text;
    if (className) node.className = className;
    return node;
}

async function load() {
    const version = ++loadVersion;
    message.textContent = 'Loading vacancies…';
    try {
        const jobs = await api('?q=' + encodeURIComponent(document.querySelector('#query').value));
        if (version !== loadVersion) return;
        const container = document.querySelector('#jobs');
        container.replaceChildren();
        document.querySelector('#count').textContent = jobs.length;
        if (!jobs.length) container.append(element('p', 'No vacancies found. Add your first opportunity or change the search.', 'empty'));
        for (const job of jobs) {
            const card = element('article', '', 'card');
            card.append(element('h3', job.title), element('p', `${job.company} · ${job.location} · ${job.experienceYears} years experience`, 'meta'), element('p', job.description, 'description'));
            const actions = element('div', '', 'actions');
            const edit = element('button', 'Edit');
            edit.onclick = () => {
                editingId = job.id;
                for (const key of ['title', 'company', 'location', 'description', 'experienceYears']) form.elements[key].value = job[key];
                document.querySelector('#form-title').textContent = 'Edit vacancy';
                document.querySelector('#cancel').hidden = false;
                formMessage.textContent = '';
                form.elements.title.focus();
            };
            const remove = element('button', 'Delete', 'danger');
            remove.onclick = async () => {
                if (!confirm(`Delete "${job.title}"?`)) return;
                remove.disabled = true;
                try {
                    await api('/' + job.id, {method: 'DELETE'});
                    if (editingId === job.id) reset();
                    await load();
                } catch (error) { message.textContent = error.message; }
                finally { remove.disabled = false; }
            };
            actions.append(edit, remove); card.append(actions); container.append(card);
        }
        message.textContent = '';
    } catch (error) { if (version === loadVersion) message.textContent = error.message; }
}

function reset() {
    editingId = null;
    form.reset();
    document.querySelector('#form-title').textContent = 'Add a vacancy';
    document.querySelector('#cancel').hidden = true;
    formMessage.textContent = '';
}
document.querySelector('#cancel').onclick = reset;
document.querySelector('#search').onsubmit = event => { event.preventDefault(); load(); };
form.onsubmit = async event => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(form));
    payload.experienceYears = Number(payload.experienceYears);
    save.disabled = true;
    formMessage.textContent = 'Saving…';
    try {
        await api(editingId === null ? '' : '/' + editingId, {
            method: editingId === null ? 'POST' : 'PUT',
            headers: {'Content-Type': 'application/json'}, body: JSON.stringify(payload)
        });
        reset(); formMessage.textContent = 'Vacancy saved.'; await load();
    } catch (error) { formMessage.textContent = error.message; }
    finally { save.disabled = false; }
};
load();
