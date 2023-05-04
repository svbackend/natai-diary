<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

final class Version20230504173357 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $this->addSql('CREATE SEQUENCE category_id_seq INCREMENT BY 1 MINVALUE 1 START 1');
        $this->addSql('CREATE SEQUENCE link_id_seq INCREMENT BY 1 MINVALUE 1 START 1');
        $this->addSql('CREATE SEQUENCE link_category_id_seq INCREMENT BY 1 MINVALUE 1 START 1');
        $this->addSql('CREATE SEQUENCE suggestion_link_id_seq INCREMENT BY 1 MINVALUE 1 START 1');
        $this->addSql('CREATE TABLE category (id INT NOT NULL, name VARCHAR(255) NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_64C19C15E237E06 ON category (name)');
        $this->addSql('CREATE TABLE link (id INT NOT NULL, url VARCHAR(255) NOT NULL, title VARCHAR(255) NOT NULL, description VARCHAR(255) NOT NULL, image VARCHAR(255) DEFAULT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_36AC99F1F47645AE ON link (url)');
        $this->addSql('CREATE TABLE link_category (id INT NOT NULL, link_id INT NOT NULL, category_id INT NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_CBE67908ADA40271 ON link_category (link_id)');
        $this->addSql('CREATE INDEX IDX_CBE6790812469DE2 ON link_category (category_id)');
        $this->addSql('CREATE TABLE suggestion_link (id INT NOT NULL, suggestion_id UUID NOT NULL, link_id INT NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_3DC956E2A41BB822 ON suggestion_link (suggestion_id)');
        $this->addSql('CREATE INDEX IDX_3DC956E2ADA40271 ON suggestion_link (link_id)');
        $this->addSql('COMMENT ON COLUMN suggestion_link.suggestion_id IS \'(DC2Type:uuid)\'');
        $this->addSql('ALTER TABLE link_category ADD CONSTRAINT FK_CBE67908ADA40271 FOREIGN KEY (link_id) REFERENCES link (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE link_category ADD CONSTRAINT FK_CBE6790812469DE2 FOREIGN KEY (category_id) REFERENCES category (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE suggestion_link ADD CONSTRAINT FK_3DC956E2A41BB822 FOREIGN KEY (suggestion_id) REFERENCES suggestion (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE suggestion_link ADD CONSTRAINT FK_3DC956E2ADA40271 FOREIGN KEY (link_id) REFERENCES link (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP SEQUENCE category_id_seq CASCADE');
        $this->addSql('DROP SEQUENCE link_id_seq CASCADE');
        $this->addSql('DROP SEQUENCE link_category_id_seq CASCADE');
        $this->addSql('DROP SEQUENCE suggestion_link_id_seq CASCADE');
        $this->addSql('ALTER TABLE link_category DROP CONSTRAINT FK_CBE67908ADA40271');
        $this->addSql('ALTER TABLE link_category DROP CONSTRAINT FK_CBE6790812469DE2');
        $this->addSql('ALTER TABLE suggestion_link DROP CONSTRAINT FK_3DC956E2A41BB822');
        $this->addSql('ALTER TABLE suggestion_link DROP CONSTRAINT FK_3DC956E2ADA40271');
        $this->addSql('DROP TABLE category');
        $this->addSql('DROP TABLE link');
        $this->addSql('DROP TABLE link_category');
        $this->addSql('DROP TABLE suggestion_link');
    }
}
