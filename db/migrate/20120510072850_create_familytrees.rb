class CreateFamilytrees < ActiveRecord::Migration
  def change
    create_table :familytrees do |t|
      t.integer :user_id
      t.string :name

      t.timestamps
    end
  end
end
