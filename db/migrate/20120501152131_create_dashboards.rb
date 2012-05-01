class CreateDashboards < ActiveRecord::Migration
  def change
    create_table :dashboards do |t|
      t.string :name
      t.text :description
      t.integer :user_id
      t.integer :dashtable_id
      t.string :dashtable_type

      t.timestamps
    end
  end
end
