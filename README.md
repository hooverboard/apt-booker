# Apt-Booker: Sistema de Agendamento de Compromissos

Site implantado: https://apt-booker.vercel.app  
obs: por favor aguarde 1–2 minutos para o servidor backend “acordar”, ele está no plano gratuito

Banco de dados: implantado no Render  
Backend: implantado no Render (Docker)  
Frontend: implantado no Vercel  

Apt-Booker é uma aplicação web full-stack que permite aos usuários marcar agendamentos com diversas lojas e possibilita que os donos das lojas gerenciem seus serviços e agendamentos.  

## Screenshots

**Página inicial**  
<img width="1440" height="900" alt="Home Page" src="https://github.com/user-attachments/assets/b6a23409-47be-4186-89a7-495d3b0d13c6" />

**Página da Loja**  
<img width="1440" height="900" alt="Shop Page" src="https://github.com/user-attachments/assets/12d7194a-52f1-482b-9da3-1a412fd7dabb" />

**Editar Loja**  
<img width="1440" height="900" alt="Edit Shop" src="https://github.com/user-attachments/assets/8ee2346b-6126-4748-bc80-64a3409d539b" />

**Visualizar/Gerenciar Agendamentos da Loja**  
<img width="1440" height="900" alt="Manage Bookings" src="https://github.com/user-attachments/assets/27c761b7-bcc7-4b16-b718-0cfd6d2642a6" />

**Editar Serviços da Loja**  
<img width="1440" height="900" alt="Edit Services" src="https://github.com/user-attachments/assets/c33fb533-e111-4462-af0a-063e26804733" />

## Funcionalidades

* **Autenticação de Usuários:** Registro e login de usuários de forma segura.   
* **Gerenciamento de Lojas:** Donos de lojas (hosts) podem criar, atualizar e excluir suas lojas.  
* **Gerenciamento de Serviços:** Hosts podem adicionar, editar e remover serviços de suas lojas, incluindo detalhes como preço e duração.  
* **Agendamento de Compromissos:** Usuários podem navegar pelas lojas e marcar agendamentos para horários disponíveis.  
* **Visualização de Compromissos:**    
  * Usuários podem visualizar seus agedamentos futuros e passados.  
  * Hosts podem visualizar e gerenciar todos os agendamentos de suas lojas.  
* **Disponibilidade Dinâmica:** O sistema calcula e exibe os horários disponíveis com base no horário de funcionamento da loja e nos agendamentos existentes.  

## Tecnologias Utilizadas  

### Backend  
* Java  
* Spring Boot  
* Spring Data JPA  
* PostgreSQL  
* Maven  

### Frontend  
* React  
* Vite  
* Axios  
* CSS  
