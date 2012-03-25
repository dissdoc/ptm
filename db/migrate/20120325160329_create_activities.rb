class CreateActivities < ActiveRecord::Migration
  def change
    create_table :activities do |t|
      t.integer :user_id
      t.string :action
      t.string :object_name
      t.string :object_link
      t.string :action2
      t.string :object_name2
      t.string :object_link2

      t.timestamps
    end
  end
end
