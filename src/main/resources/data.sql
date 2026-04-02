-- @formatter:off
-- ============================================================
-- EventHub — Seed Data: 15 Real Events
-- Run AFTER the app starts once (schema auto-created by JPA)
-- ============================================================

-- ── 1. Admin user (password: Admin@1234) ────────────────────
INSERT IGNORE INTO users (first_name, last_name, email, password, phone, role, enabled, created_at, updated_at)
VALUES (
  'Admin', 'EventHub',
  'admin@eventhub.com',
  '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- Admin@1234 bcrypt
  '+91-9876543210',
  'ROLE_ADMIN',
  true,
  NOW(), NOW()
);

-- ── 10 Speakers ─────────────────────────────────────────────
INSERT IGNORE INTO speakers (first_name, last_name, email, title, organization, bio, linkedin_url, website_url, created_at, updated_at) VALUES
('Sundar',    'Pichai',    'sundar.p@speakers.io',   'CEO',                        'Google',              'Sundar Pichai is the CEO of Alphabet Inc. and Google. Under his leadership, Google has expanded its product ecosystem to serve billions of users worldwide.',                                                              'https://linkedin.com/in/sundarpichai',    'https://about.google',          NOW(), NOW()),
('Fei-Fei',  'Li',        'feifei.li@speakers.io',  'Professor & Co-Director',    'Stanford HAI',        'Dr. Fei-Fei Li is a world-renowned AI researcher, Professor at Stanford University, and co-director of Stanford Human-Centered AI Institute. Formerly Chief Scientist of AI/ML at Google Cloud.',                    'https://linkedin.com/in/feifeili',        'https://profiles.stanford.edu/fei-fei-li', NOW(), NOW()),
('Satya',    'Nadella',   'satya.n@speakers.io',    'CEO',                        'Microsoft',           'Satya Nadella transformed Microsoft into a cloud-first, AI-first company. He is the author of "Hit Refresh" and has driven Microsoft\'s market cap past $3 trillion.',                                              'https://linkedin.com/in/satyanadella',    'https://microsoft.com',         NOW(), NOW()),
('Priya',    'Natarajan', 'priya.n@speakers.io',    'Astrophysicist & Author',    'Yale University',     'Priya Natarajan is a theoretical astrophysicist at Yale whose research focuses on dark matter, dark energy, and black holes. She has authored several acclaimed popular science books.',                             'https://linkedin.com/in/priyanatarajan',  'https://priyastuff.com',        NOW(), NOW()),
('Jensen',   'Huang',     'jensen.h@speakers.io',   'CEO & Founder',              'NVIDIA',              'Jensen Huang co-founded NVIDIA in 1993 and pioneered GPU computing. He drove NVIDIA to become the world\'s most valuable semiconductor company and the backbone of modern AI infrastructure.',                        'https://linkedin.com/in/jenhsunhuang',    'https://nvidia.com',            NOW(), NOW()),
('Reshma',   'Saujani',   'reshma.s@speakers.io',   'Founder & CEO',              'Girls Who Code',      'Reshma Saujani is the founder of Girls Who Code, which has reached 500,000+ girls in the US. She is a leading advocate for gender diversity in tech and author of "Brave, Not Perfect".',                            'https://linkedin.com/in/reshmasaujani',   'https://reshmasaujani.com',     NOW(), NOW()),
('Andrew',   'Ng',        'andrew.ng@speakers.io',  'Co-Founder',                 'Coursera / DeepLearning.AI', 'Andrew Ng is a pioneer in deep learning and AI education. He co-founded Coursera, founded DeepLearning.AI, and led AI teams at Google and Baidu. His courses have reached over 7 million learners.',       'https://linkedin.com/in/andrewyng',       'https://deeplearning.ai',       NOW(), NOW()),
('Nandan',   'Nilekani',  'nandan.n@speakers.io',   'Co-Founder & Chairman',      'Infosys',             'Nandan Nilekani co-founded Infosys and served as Chairman of UIDAI, the body that created Aadhaar — the world\'s largest biometric ID system covering 1.4 billion Indians. He is a Padma Bhushan awardee.',         'https://linkedin.com/in/nandannilekani',  'https://nilekani.org',          NOW(), NOW()),
('Kiran',    'Mazumdar-Shaw', 'kiran.m@speakers.io','Founder & Executive Chairperson', 'Biocon',          'Kiran Mazumdar-Shaw is India\'s most successful biotech entrepreneur. She founded Biocon in 1978 with ₹10,000 and built it into a $3B global biopharmaceutical company. She is a Padma Vibhushan awardee.',         'https://linkedin.com/in/kiranmazumdarshaw', 'https://biocon.com',          NOW(), NOW()),
('Vint',     'Cerf',      'vint.cerf@speakers.io',  'Vice President & Chief Internet Evangelist', 'Google', 'Vint Cerf co-designed the TCP/IP protocols that underpin the entire internet. Known as one of the "Fathers of the Internet," he is a Turing Award recipient and Google\'s Chief Internet Evangelist.',             'https://linkedin.com/in/vintoncerf',      'https://internetsociety.org',   NOW(), NOW());

-- ── 15 Events ────────────────────────────────────────────────

-- Event 1
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'Google I/O Extended Bangalore 2025',
  'Join us for the biggest Google developer festival of the year, hosted right here in Bangalore. Google I/O Extended brings the best of Google I/O to your city — featuring live-streamed keynotes, hands-on codelabs with Gemini AI, Firebase, Flutter, and Android 15, plus deep-dive sessions led by local Google Developer Experts. Attendees get early access to new Google APIs, swag bags, and direct networking with Google engineers visiting from Mountain View.',
  '2025-08-15 09:00:00',
  '2025-08-15 18:00:00',
  'Bangalore, Karnataka, India',
  'Koramangala Indoor Stadium',
  'CONFERENCE',
  1200,
  true,
  'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800',
  NOW(), NOW()
);

-- Event 2
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'The AI Summit India 2025',
  'India''s premier artificial intelligence summit returns for its fifth edition, bringing together 2,000+ AI practitioners, researchers, and enterprise leaders under one roof. This two-day summit features keynotes from global AI leaders, panel discussions on responsible AI, workshops on LLMs and generative AI in enterprise, and an AI startup pitch competition with ₹50 lakh in prizes. Topics include AI in healthcare, Bharat-specific large language models, AI regulation, and the future of human-AI collaboration.',
  '2025-09-10 08:30:00',
  '2025-09-11 17:30:00',
  'Mumbai, Maharashtra, India',
  'Bombay Exhibition Centre, Goregaon',
  'CONFERENCE',
  2000,
  true,
  'https://images.unsplash.com/photo-1677442135703-1787eea5ce01?w=800',
  NOW(), NOW()
);

-- Event 3
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'Full-Stack Web Development Bootcamp',
  'An intensive 2-day hands-on bootcamp designed for developers who want to go from zero to production-ready full-stack skills. Day 1 covers React 19, TypeScript, Tailwind CSS, and modern component architecture. Day 2 dives into Spring Boot 3, REST API design, JWT authentication, MySQL, and cloud deployment on AWS. Participants leave with a fully deployed capstone project, a certificate of completion, and access to a private alumni Slack community.',
  '2025-09-20 09:00:00',
  '2025-09-21 17:00:00',
  'Hyderabad, Telangana, India',
  'T-Hub, Raidurgam',
  'WORKSHOP',
  80,
  true,
  'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800',
  NOW(), NOW()
);

-- Event 4
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'Cloud & DevOps Summit 2025',
  'A deep-dive conference for cloud architects, DevOps engineers, and platform teams. Three tracks run in parallel: Cloud Native (Kubernetes, service mesh, eBPF), DevSecOps (shift-left security, SBOMs, supply-chain attacks), and Platform Engineering (internal developer platforms, golden paths, backstage). Sponsored by AWS, Google Cloud, and HashiCorp. Certified practitioners receive 12 CPD credits. The day ends with a live infrastructure-as-code hackathon.',
  '2025-10-03 09:00:00',
  '2025-10-03 19:00:00',
  'Pune, Maharashtra, India',
  'Courtyard by Marriott, Hinjewadi',
  'CONFERENCE',
  600,
  true,
  'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800',
  NOW(), NOW()
);

-- Event 5
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'Women in Tech Leadership Forum',
  'A transformative full-day forum celebrating and empowering women leaders in technology. The programme includes keynotes from CxOs at Infosys, Flipkart, and Razorpay, panel discussions on navigating the C-suite, workshops on executive presence and salary negotiation, and a structured mentorship speed-dating session connecting mid-career women engineers with senior leaders. The forum culminates in the annual "She Leads Tech" award ceremony recognising 10 outstanding women in Indian tech.',
  '2025-10-18 09:00:00',
  '2025-10-18 18:00:00',
  'Bangalore, Karnataka, India',
  'The Leela Palace, Old Airport Road',
  'CONFERENCE',
  400,
  true,
  'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=800',
  NOW(), NOW()
);

-- Event 6
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'Product Management Masterclass: From 0 to PMF',
  'A one-day intensive masterclass for product managers and founders obsessed with achieving product-market fit. Led by former product leaders from Swiggy, Zepto, and Notion, the class covers: customer discovery interviews, prioritisation frameworks (RICE, ICE, Kano), writing airtight PRDs, designing for behaviour change, growth loops, and North Star metrics. Limited to 60 participants to ensure a highly interactive, workshop-style format with real case studies.',
  '2025-11-01 10:00:00',
  '2025-11-01 18:00:00',
  'Bangalore, Karnataka, India',
  'WeWork Galaxy, Residency Road',
  'WORKSHOP',
  60,
  true,
  'https://images.unsplash.com/photo-1552664730-d307ca884978?w=800',
  NOW(), NOW()
);

-- Event 7
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'Cybersecurity India Conference (CSICON) 2025',
  'CSICON is India''s largest independent cybersecurity conference, drawing 1,500 security professionals, ethical hackers, and CISO-level executives annually. The 2025 agenda covers zero-trust architecture, AI-powered threat detection, ransomware response playbooks, DPDP Act compliance, and red team vs. blue team live exercises. A 24-hour CTF (Capture the Flag) challenge runs alongside the main conference with ₹10 lakh in prizes. Qualifies for (ISC)² CPE credits.',
  '2025-11-14 08:00:00',
  '2025-11-15 20:00:00',
  'Delhi, India',
  'Pragati Maidan, Hall 7',
  'CONFERENCE',
  1500,
  true,
  'https://images.unsplash.com/photo-1563986768609-322da13575f3?w=800',
  NOW(), NOW()
);

-- Event 8
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'Startup Founders Networking Night — Bangalore',
  'The most curated startup networking event in Bangalore''s ecosystem. 200 carefully vetted founders, investors, and operators gather for an evening of structured 5-minute speed networking rounds, investor office hours (10-minute 1:1 slots with 15 active VCs including Peak XV, Accel, and Blume), and a live pitch segment where 5 startups pitch to a live audience for ₹5 lakh in non-dilutive grants. Strictly no recruiters or service providers — founders only.',
  '2025-11-21 18:00:00',
  '2025-11-21 22:00:00',
  'Bangalore, Karnataka, India',
  'Draper Startup House, Indiranagar',
  'NETWORKING',
  200,
  true,
  'https://images.unsplash.com/photo-1511795409834-432f7b1728b2?w=800',
  NOW(), NOW()
);

-- Event 9
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'ReactConf India 2025',
  'The definitive React conference for the Indian developer community. Two tracks, one day: the Main Stage features talks on React 19 concurrent features, Server Components in production, React Compiler deep-dives, and state management in 2025. The Workshop Track runs 3 hands-on sessions capped at 30 devs each on React Native, Next.js 15 App Router, and testing React applications with Vitest and Playwright. Speakers include React core team contributors and OSS maintainers.',
  '2025-12-06 09:00:00',
  '2025-12-06 18:30:00',
  'Bangalore, Karnataka, India',
  'ITC Gardenia, Residency Road',
  'CONFERENCE',
  500,
  true,
  'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800',
  NOW(), NOW()
);

-- Event 10
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'Data Engineering & MLOps Summit',
  'A technical summit for data engineers, ML engineers, and analytics leaders building modern data stacks and ML pipelines at scale. Sessions cover: dbt + Airflow + Iceberg for lakehouse architectures, real-time streaming with Apache Flink, feature stores (Feast, Tecton), ML experiment tracking with MLflow, model serving with Triton and BentoML, LLMOps with LangSmith, and cost optimisation on Snowflake and BigQuery. Endorsed by the Data Engineering Community India (DECI).',
  '2025-12-13 09:00:00',
  '2025-12-13 18:00:00',
  'Chennai, Tamil Nadu, India',
  'Chennai Trade Centre, Nandambakkam',
  'SEMINAR',
  700,
  true,
  'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800',
  NOW(), NOW()
);

-- Event 11
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'Blockchain & Web3 Developer Summit',
  'India''s leading Web3 technical conference for blockchain developers, DeFi builders, and crypto entrepreneurs. The 2025 edition focuses on Ethereum''s roadmap post-Merge, Solana ecosystem development, zero-knowledge proofs and zk-rollups, cross-chain interoperability protocols, real-world asset (RWA) tokenisation under SEBI''s new sandbox, and building compliant crypto applications under India''s evolving regulatory landscape. Includes a 48-hour hackathon with $50,000 in prizes from Polygon and Binance Labs.',
  '2026-01-17 09:00:00',
  '2026-01-18 18:00:00',
  'Mumbai, Maharashtra, India',
  'Jio World Convention Centre, BKC',
  'CONFERENCE',
  900,
  true,
  'https://images.unsplash.com/photo-1639762681485-074b7f938ba0?w=800',
  NOW(), NOW()
);

-- Event 12
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'UX & Design Systems Workshop',
  'A practical one-day workshop for product designers, design leads, and front-end engineers who want to build and maintain scalable design systems. Facilitated by senior designers from Razorpay, Zomato, and Adobe India, the workshop covers: design tokens and theming, component API design, accessibility (WCAG 2.2), documentation with Storybook 8, contribution models for distributed teams, and measuring adoption. Participants work in cross-functional teams on a real-world design system sprint.',
  '2026-02-07 10:00:00',
  '2026-02-07 18:00:00',
  'Bangalore, Karnataka, India',
  'Adobe India Office, Divyashree Techno Park',
  'WORKSHOP',
  75,
  true,
  'https://images.unsplash.com/photo-1561070791-2526d30994b5?w=800',
  NOW(), NOW()
);

-- Event 13
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'Open Source India (OSI) 2026',
  'Open Source India is a three-day community-driven conference celebrating open-source software, hardware, and culture. The 2026 edition features 60+ talks across tracks: Linux & Systems, Cloud Native, AI/ML OSS, Developer Tools, Open Data, and Community Building. Hallway track, contributor workshops, maintainer office hours, and a dedicated "First Contribution" zone help newcomers make their first open-source pull request live on stage. Sponsored by Red Hat, CNCF, and GitHub.',
  '2026-02-20 09:00:00',
  '2026-02-22 17:00:00',
  'Bangalore, Karnataka, India',
  'NIMHANS Convention Centre, Hosur Road',
  'CONFERENCE',
  3000,
  true,
  'https://images.unsplash.com/photo-1556075798-4825dfaaf498?w=800',
  NOW(), NOW()
);

-- Event 14
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'HealthTech Innovation Summit 2026',
  'A first-of-its-kind summit at the intersection of healthcare and technology, gathering clinicians, hospital CIOs, healthtech founders, and medical device engineers. Sessions cover AI-assisted diagnostics, telemedicine 2.0 regulatory frameworks, ABDM (Ayushman Bharat Digital Mission) integration, remote patient monitoring with IoT wearables, precision medicine and genomics, and mental health platforms. Startup showcase features 20 healthtech companies with live product demos. CME-accredited for practising physicians.',
  '2026-03-14 09:00:00',
  '2026-03-15 17:00:00',
  'Hyderabad, Telangana, India',
  'HICC, Novotel Hyderabad Convention Centre',
  'CONFERENCE',
  1000,
  true,
  'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=800',
  NOW(), NOW()
);

-- Event 15
INSERT IGNORE INTO events (title, description, start_date_time, end_date_time, location, venue, category, max_capacity, is_active, image_url, created_at, updated_at) VALUES (
  'Java & Spring Boot Deep Dive — 3-Day Workshop',
  'The most comprehensive Java workshop in India, designed for developers who want to master enterprise Java development with Spring Boot 3.3, Spring Security 6, and the Spring ecosystem. Day 1: Core Java 21 features (virtual threads, records, sealed classes), Spring Boot internals and auto-configuration. Day 2: Spring Data JPA with Hibernate 6, reactive programming with WebFlux, Spring Security with JWT and OAuth2. Day 3: Microservices with Spring Cloud, distributed tracing with Micrometer, Kafka integration, and production deployment on Kubernetes. Limited to 50 participants.',
  '2026-04-10 09:00:00',
  '2026-04-12 17:00:00',
  'Pune, Maharashtra, India',
  'Persistent Systems Campus, Senapati Bapat Road',
  'WORKSHOP',
  50,
  true,
  'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800',
  NOW(), NOW()
);

-- ── Link speakers to events ──────────────────────────────────
INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'Google I/O Extended Bangalore 2025' AND s.email = 'sundar.p@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'The AI Summit India 2025' AND s.email = 'feifei.li@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'The AI Summit India 2025' AND s.email = 'andrew.ng@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'The AI Summit India 2025' AND s.email = 'jensen.h@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'Full-Stack Web Development Bootcamp' AND s.email = 'vint.cerf@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'Cloud & DevOps Summit 2025' AND s.email = 'satya.n@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'Women in Tech Leadership Forum' AND s.email = 'reshma.s@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'Women in Tech Leadership Forum' AND s.email = 'kiran.m@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'Startup Founders Networking Night — Bangalore' AND s.email = 'nandan.n@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'Data Engineering & MLOps Summit' AND s.email = 'andrew.ng@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'HealthTech Innovation Summit 2026' AND s.email = 'priya.n@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'Open Source India (OSI) 2026' AND s.email = 'vint.cerf@speakers.io';

INSERT IGNORE INTO event_speakers (event_id, speaker_id)
SELECT e.id, s.id FROM events e JOIN speakers s
  ON e.title = 'Blockchain & Web3 Developer Summit' AND s.email = 'satya.n@speakers.io';

