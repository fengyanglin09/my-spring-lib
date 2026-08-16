# Lesson 21: Server-Rendered MVC With `@Controller`

## Status

Planned optional lesson. This folder exists as a reminder that regular
`@Controller` is important, but it belongs to a server-rendered MVC branch rather
than the main REST API path.

## Objective

Learn how Spring MVC renders HTML pages with regular `@Controller`, view names,
template data, form submissions, and redirects.

## Why This Is Separate From Lesson 07

Lesson 07 teaches `@RestController`, which is the common choice for backend REST
APIs that return JSON.

This lesson is about regular `@Controller`, which is commonly used when the
server returns pages instead of JSON.

The difference is:

```text
@RestController
-> return value becomes the HTTP response body
-> common output: JSON
-> common client: another service, frontend app, mobile app, API client

@Controller
-> return value is often treated as a view/template name
-> common output: HTML
-> common client: browser loading server-rendered pages
```

`@RestController` is basically:

```text
@Controller + @ResponseBody
```

So a REST controller says:

```text
Do not look for a page template.
Write my return value directly into the response body.
```

A regular controller often says:

```text
Use my return value to find the page/template that should be rendered.
```

## Planned Study

- Create a regular `@Controller`.
- Return a view name from a controller method.
- Add a template engine such as Thymeleaf.
- Use `Model` to pass Java data into an HTML template.
- Handle browser form submissions.
- Redirect after successful form posts.
- Compare `@Controller` plus `@ResponseBody` with `@RestController`.
- Test server-rendered MVC with MockMvc.

## Future Folder Shape

When this lesson becomes hands-on, it will likely need this shape:

```text
lesson21_server_rendered_mvc_controller/
├── README.md
├── package-info.java
├── api/
│   └── Lesson21PageController.java
├── model/
│   ├── Lesson21ProfileForm.java
│   └── Lesson21ProfileView.java
└── service/
    └── Lesson21ProfileService.java
```

It will also likely need templates under `src/main/resources/templates/`.

## Main Takeaway

Use `@RestController` when the endpoint is an API returning data.

Use regular `@Controller` when the endpoint is part of a server-rendered page
flow.
